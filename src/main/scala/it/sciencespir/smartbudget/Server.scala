package it.sciencespir

import it.sciencespir.smartbudget.http.HTTPServices
import it.sciencespir.smartbudget.DB.DB
import it.sciencespir.smartbudget.DB.service.{CategoriesService, OperationsService, UsersService}
import it.sciencespir.smartbudget.util.Executor
import org.http4s.server.blaze.BlazeBuilder

import scalaz.concurrent.Task
import org.http4s.server.ServerApp



object SmartBudgetServer extends ServerApp {
  implicit val database = DB.database
  implicit val executorService = Executor()
  implicit val categoriesService = new CategoriesService()
  implicit val operationsService = new OperationsService()
  implicit val usersService = new UsersService()



  val services = Task.gatherUnordered(List(categoriesService, operationsService, usersService).map(_.initializeIfNeeded()))

  override def server(args: List[String]) = for {
    _ <- services.map(_ => Unit)
    server <- BlazeBuilder
      .withServiceExecutor(executorService)
      .bindHttp(8081)
      .mountService(HTTPServices())
      .start
  } yield server

}
