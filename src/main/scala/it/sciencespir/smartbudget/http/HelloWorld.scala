package it.sciencespir.smartbudget.http

import _root_.argonaut.Argonaut._
import org.http4s._
import org.http4s.argonaut._
import org.http4s.dsl._
import org.http4s.server.syntax._

object SmartBudgetService {
  val operationsService = HttpService {
    case GET -> Root / "operations" / name =>
      Ok(jSingleObject("message", jString(s"Hello, ${name}")))
  }
  val categoriesService = HttpService {
    case GET -> Root / "api" =>
      Ok()
  }

  val aggregateService = operationsService orElse categoriesService
}



