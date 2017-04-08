package it.sciencespir.smartbudget.http

import it.sciencespir.smartbudget.DB.model.{CRUD, Category}
import it.sciencespir.smartbudget.DB.service.{CRUDService, CategoriesService}
import org.http4s.{EntityDecoder, EntityEncoder, HttpService}
import org.http4s.dsl._


import scala.util.Try


/**
  * Created by kamilbadyla on 21/01/17.
  */

object CRUDResource {
  def apply[T <: CRUD](basePath: String)(implicit
                                         modelService: CRUDService[T, _],
                                         jsonEncoder: EntityEncoder[T],
                                         jsonListEncoder: EntityEncoder[List[T]],
                                         jsonDecoder: EntityDecoder[T]) = HttpService {

    case GET -> Root / basePath =>
      modelService.list
        .map(_.toList)
        .flatMap(Ok(_))

    case request@POST -> Root / `basePath` =>
      request.decode[T] { model =>
        modelService.create(model)
          .flatMap(_ => Created())
      }

    case GET -> Root / `basePath` / IdVar(id) =>
      modelService.find(id) flatMap {
        case Some(model) => Ok.apply(model)
        case None => NotFound()
      }

    case DELETE -> Root / `basePath` / IdVar(id) =>
      modelService.find(id) flatMap {
        case Some(model) => modelService.delete(model)
          .flatMap(_ => NoContent())
        case None => NotFound()
      }
  }
}



object IdVar {
  def unapply(string: String): Option[Int] =
    Try(string.toInt).toOption
}