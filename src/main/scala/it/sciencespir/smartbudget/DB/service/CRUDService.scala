package it.sciencespir.smartbudget.DB.service

import java.util.UUID
import java.util.concurrent.ExecutorService

import it.sciencespir.smartbudget.DB.model._
import it.sciencespir.smartbudget.DB.query._
import it.sciencespir.smartbudget.DB.table.CRUDTable
import slick.backend.DatabaseComponent
import slick.dbio.{DBIO, Effect, NoStream}
import com.github.t3hnar.bcrypt._


import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scalaz.{-\/, EitherT, \/, \/-}
import scalaz.concurrent.Task
import com.typesafe.scalalogging.StrictLogging
import it.sciencespir.smartbudget.DB.driver.PGDriver.api._

/**
  * Created by kamilbadyla on 20/01/17.
  */

abstract class CRUDService[M <: CRUD, +MT <: CRUDTable[M]](query: CRUDQuery[M, MT])(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends StrictLogging {

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


//  type Sql[+R] = SqlAction[R, NoStream, Effect.All]

  protected def task[R](action: DBIO[R]): Task[R] = {
    implicit val executionContext = ExecutionContext.fromExecutorService(executorService)
    Task.async { cb =>
//      println("SQL Statements" + action.statements.foreach(println))
      database.run(action) onComplete {
        case Success(x) => cb(\/-(x))
        case Failure(x) => cb(-\/(x))
      }
    }
  }
}

class CategoriesService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends CRUDService(categories)
class UsersService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends CRUDService(users) {
  def login(userLogin: UserLoginForm) =
    find(userLogin.email).map(_.filter(user => userLogin.password.isBcrypted(user.password_hash)).map(UserAuth(_)))

  def find(email: String): Task[Option[User]] =
    task(users.find(email)).map(_.headOption)

}
class OperationsService(implicit database: DatabaseComponent#DatabaseDef, executorService: ExecutorService) extends CRUDService(operations)
