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
  this: TablesComponent with QueryComponent with DBConfigProvider =>

  import dbConfig.driver.api._

  abstract class CRUDService[M <: CRUD, +MT <: CRUDTable[M]](query: QueryComponent#CRUDQuery[M, MT])(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends StrictLogging {

    implicit val executionContext = ExecutionContext.fromExecutorService(executorService)

    def initialize() =
      task(query.initialize)

    def initializeIfNeeded() =
      task(query.initializeIfNeeded)

    def find(id: Int): Task[Option[M]] =
      task(query.find(id)).map(_.headOption)

    def create(model: M): Task[M] =
      task(query.insert(model)).map(_ => model)

    def update(model: M): Task[M] =
      task(query.update(model)).map(_ => model)

    def delete(model: M): Task[_] =
      task(query.delete(model))

    def list(): Task[Seq[M]] =
      task(query.list)

    protected def task[R](action: DBIO[R]): Task[R] = {
      implicit val executionContext = ExecutionContext.fromExecutorService(executorService)
      Task.async { cb =>
        database.run(action) onComplete {
          case Success(x) => cb(\/-(x))
          case Failure(x) => cb(-\/(x))
        }
      }
    }
  }

  class CategoriesService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends CRUDService(categories)
  object categoriesService extends CategoriesService

  class UsersService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends CRUDService(users) {
    def login(userLogin: UserLoginForm) =
      find(userLogin.email).map(_.filter(user => userLogin.password.isBcrypted(user.password_hash)).map(UserAuth(_)))

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


