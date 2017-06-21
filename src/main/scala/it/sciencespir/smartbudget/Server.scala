package it.sciencespir

import it.sciencespir.smartbudget.DB.driver.{DevDatabaseComponent, DevelopmentDBConfig}
import it.sciencespir.smartbudget.http.HTTPServices
import it.sciencespir.smartbudget.util.Executor
import org.http4s.server.blaze.BlazeBuilder

import scalaz.concurrent.Task
import org.http4s.server.ServerApp

import scalaz._
import Scalaz._
import it.sciencespir.smartbudget.DB.service.FactualCategories
import com.typesafe.scalalogging.StrictLogging


object DefaultHTTPServices extends HTTPServices with DevDatabaseComponent

object SmartBudgetServer extends ServerApp with StrictLogging {

  implicit val categoriesService = DefaultHTTPServices.categoriesService
  implicit val operationsService = DefaultHTTPServices.operationsService
  implicit val placesService = DefaultHTTPServices.placesService
  implicit val usersService = DefaultHTTPServices.usersService
  implicit val factualCategories = DefaultHTTPServices.factualCategories
  implicit val placeFactualCategories = DefaultHTTPServices.placeFactualCategories
  val services = Task.gatherUnordered(List(categoriesService, placesService, operationsService, usersService).map(_.initializeIfNeeded()))
//    .flatMap(_ => placesService.initializeData(FactualCategories.apiCategories))

  override def server(args: List[String]) = for {
    _ <- services.map{ _ => () }
    server <- BlazeBuilder
      .withServiceExecutor(Executor())
      .bindHttp(8081, "0.0.0.0")
      .mountService(DefaultHTTPServices())
      .start
  } yield server

}
