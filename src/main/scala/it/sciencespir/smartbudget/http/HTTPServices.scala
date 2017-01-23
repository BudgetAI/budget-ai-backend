package it.sciencespir.smartbudget.http

import it.sciencespir.smartbudget.DB.model.{Category, Operation, User}
import it.sciencespir.smartbudget.DB.service.{CategoriesService, OperationsService, UsersService}
import Encoders._
import Codecs._
import it.sciencespir.smartbudget.DB.model.CategoryJSON._
import it.sciencespir.smartbudget.DB.model.OperationJSON._
import it.sciencespir.smartbudget.DB.model.UserJSON._
import org.http4s.HttpService
import org.http4s.dsl._
import org.http4s.server.syntax._

/**
  * Created by kamilbadyla on 21/01/17.
  */
object HTTPServices {
  def apply()(implicit categoriesService: CategoriesService, operationsService: OperationsService, usersService: UsersService): HttpService = {
    val filters = (LoggingFilter andThen GZipFilter)
    return filters(test() orElse CRUDResource[User]("users") orElse CRUDResource[Category]("categories") orElse CRUDResource[Operation]("operations"))
  }

  def test(): HttpService = HttpService {
    case POST -> Root / "test" =>
      Ok(User(1, "asdf"))
  }

}
