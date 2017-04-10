package it.sciencespir

import it.sciencespir.smartbudget.DB.driver.{DevDatabaseComponent, DevelopmentDBConfig}
import it.sciencespir.smartbudget.http.HTTPServices
import it.sciencespir.smartbudget.util.Executor
import org.http4s.server.blaze.BlazeBuilder

import scalaz.concurrent.Task
import org.http4s.server.ServerApp

import scalaz._
import Scalaz._


object DefaultHTTPServices extends HTTPServices with DevDatabaseComponent

object SmartBudgetServer extends ServerApp {

  implicit val categoriesService = DefaultHTTPServices.categoriesService
  implicit val operationsService = DefaultHTTPServices.operationsService
  implicit val usersService = DefaultHTTPServices.usersService
  val services = Task.gatherUnordered(List(categoriesService, operationsService, usersService).map(_.initializeIfNeeded()))

  override def server(args: List[String]) = for {
    _ <- services.map(_ => Unit)
    server <- BlazeBuilder
      .withServiceExecutor(Executor())
      .bindHttp(8081)
      .mountService(DefaultHTTPServices())
      .start
  } yield server

}
