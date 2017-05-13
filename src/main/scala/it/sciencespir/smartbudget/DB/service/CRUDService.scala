package it.sciencespir.smartbudget.DB.service

import java.util.UUID
import java.util.concurrent.ExecutorService

import it.sciencespir.smartbudget.DB.model._
import it.sciencespir.smartbudget.DB.query._
import slick.backend.DatabaseComponent
import com.github.t3hnar.bcrypt._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scalaz.{-\/, EitherT, \/, \/-}
import scalaz.concurrent.Task
import com.typesafe.scalalogging.StrictLogging
import it.sciencespir.smartbudget.DB.driver.DBConfigProvider
import it.sciencespir.smartbudget.DB.table.TablesComponent

/**
 * Created by kamilbadyla on 20/01/17.
 */

trait ServicesComponent {
  this: TablesComponent with QueryComponent with DBConfigProvider ⇒

  import dbConfig.driver.api._

  abstract class DBTService[M, MT <: Table[M]](query: QueryComponent#DBTQuery[M, MT])(implicit db: DatabaseComponent#DatabaseDef, executorService: ExecutorService) {

    implicit val executionContext = ExecutionContext.fromExecutorService(executorService)

    def initialize() =
      task(query.initialize)

    def initializeIfNeeded() =
      task(query.initializeIfNeeded)

    def list(): Task[Seq[M]] =
      task(query.list)

    def create(model: M): Task[M] =
      task(query.insert(model)).map(_ ⇒ model)

    protected def task[R](action: DBIO[R]): Task[R] = {
      implicit val executionContext = ExecutionContext.fromExecutorService(executorService)
      Task.async { cb ⇒
        db.run(action) onComplete {
          case Success(x) ⇒ cb(\/-(x))
          case Failure(x) ⇒ cb(-\/(x))
        }
      }
    }

  }

  abstract class CRUDService[M <: CRUD, MT <: CRUDTable[M]](query: QueryComponent#CRUDQuery[M, MT])(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends DBTService[M, MT](query) with StrictLogging {

    def find(id: Int): Task[Option[M]] =
      task(query.find(id)).map(_.headOption)

    def update(model: M): Task[M] =
      task(query.update(model)).map(_ ⇒ model)

    def delete(model: M): Task[_] =
      task(query.delete(model))
  }

  class CategoriesService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends CRUDService(categories)
  object categoriesService extends CategoriesService

  class PlacesService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends DBTService(places) {

    override def initializeIfNeeded() = {
      task(places.initializeIfNeeded)
        .flatMap(_ ⇒ task(factualCategories.initializeIfNeeded))
        .flatMap(_ ⇒ task(placeFactualCategories.initializeIfNeeded))
        .flatMap(_ ⇒ FactualCategories.apiCategories
          .flatMap(cats ⇒ this.create(cats.data).map(_ ⇒ Unit)))
    }

    def create(placewc: PlaceWithCategories): Task[Place] =
      for {
        _ ← task(places.insertOrUpdate(placewc.place))
        _ ← task(DBIO.sequence(placewc.categories.map(c ⇒ placeFactualCategories.insertOrUpdate(PlaceFactualCategory(placewc.place.factual_place_id, c)))))
      } yield placewc.place

    def create(categories: List[FactualCategory]): Task[List[FactualCategory]] = {
      val toBeInserted = categories
        .sortWith(_.parent_id.getOrElse(0) < _.parent_id.getOrElse(0))
        .map { row ⇒ factualCategories.insertOrUpdate(row) }
      val inOneGo = DBIO.sequence(toBeInserted)
      task(inOneGo).map(_ ⇒ categories)
    }
  }

  object placesService extends PlacesService

  class UsersService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends CRUDService(users) {
    def login(userLogin: UserLoginForm) =
      find(userLogin.email).map(_.filter(user ⇒ userLogin.password.isBcrypted(user.password_hash)).map(UserAuth(_)))

    def find(email: String): Task[Option[User]] =
      task(users.find(email)).map(_.headOption)

  }
  object usersService extends UsersService

  class OperationsService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends CRUDService(operations) {
    def list(userId: Int): Task[Seq[Operation]] =
      task(operations.list(userId))
  }
  object operationsService extends OperationsService
}

