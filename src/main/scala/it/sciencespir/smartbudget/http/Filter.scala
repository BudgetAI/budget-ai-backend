package it.sciencespir.smartbudget.http

import com.typesafe.scalalogging.LazyLogging
import org.http4s.{HttpService, Request, Service}
import com.github.nscala_time.time.Imports._
import org.http4s.server.middleware.GZip

/**
  * Created by kamilbadyla on 21/01/17.
  */

object GZipFilter extends HttpFilter {
  override def apply(service: HttpService): HttpService = GZip(service)
}

abstract class HttpFilter extends (HttpService => HttpService)

object LoggingFilter extends HttpFilter with LazyLogging {
  def apply(service: HttpService): HttpService = Service.lift {
    (request: Request) =>
      val start = DateTime.now()
      service(request) map { response =>
        val duration = start to DateTime.now()
        logger.info(s"${response.status.code} ${request.method} ${request.uri} ${duration}")
        response
      }
  }
}
