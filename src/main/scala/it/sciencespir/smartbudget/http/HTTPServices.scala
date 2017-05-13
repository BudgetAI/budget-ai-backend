package it.sciencespir.smartbudget.http

import it.sciencespir.smartbudget.DB.model._
import it.sciencespir.smartbudget.DB.model.User._
import it.sciencespir.smartbudget.DB.model.PlaceJSON._
import Encoders._
import it.sciencespir.smartbudget.DB.model.CategoryJSON._
import it.sciencespir.smartbudget.DB.model.OperationJSON._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.syntax._

import scalaz.{Category => _, _}
import scalaz.Scalaz._
import it.sciencespir.smartbudget.DB.validator.Validation._
import APIError._
import it.sciencespir.smartbudget.DB.driver.DatabaseComponent
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

import scala.util.Try
import scalaz.concurrent.Task

/**
  * Created by kamilbadyla on 21/01/17.
  */
trait HTTPServices {
  this: DatabaseComponent =>

  def apply()(implicit categoriesService: CategoriesService, operationsService: OperationsService, usersService: UsersService, placesService: PlacesService): HttpService = {
    val filters = (LoggingFilter andThen GZipFilter)
    return filters(HTTPLoginService() orElse CRUDResource[Category]("categories") orElse HTTPLoginService.authMiddleware(OperationsResource()) orElse HTTPLoginService.authMiddleware(PlacesResource()))
  }

  object HTTPLoginService {

    val onFailure: AuthedService[String] = Kleisli(req => Forbidden(req.authInfo))
    val authUser: Service[Request, String \/ UserProfile] = Kleisli({ request =>
      val message = for {
        header <- request.headers.get(Authorization).toRightDisjunction("Couldn't find an Authorization header")
        userprofile <- UserAuth.userProfile(header.value).toRightDisjunction("Authorisation token invalid")
        message <- \/.fromTryCatchNonFatal(userprofile).leftMap(_.toString)
      } yield message
      message.traverse(Task.now)
    })

    val authMiddleware = AuthMiddleware(authUser, onFailure)

    def apply()(implicit usersService: UsersService) = HttpService {
      case request@POST -> Root / "users" / "sign_up" =>
        request.decode[UserForm] { userForm =>
          val r = for {
            user <- User.validateTask(userForm).leftMap(ruleViolationToAPIError(_))
            u <- EitherT(usersService.create(user).attempt.map(_.leftMap(throwableToAPIError(_))))
          } yield u
          r.fold(_.toHttpResponse(request.httpVersion), user => Ok(toUserProfile(user))).join
        }

      case request@POST -> Root / "users" / "log_in" =>
        request.decode[UserLoginForm] { userLogin =>
          usersService.login(userLogin).map(_.fold(Forbidden())(Ok(_))).join
        }
    }

  }

  object CRUDResource {
    def apply[T <: CRUD](basePath: String)(implicit
                                           modelService: CRUDService[T, _],
                                           jsonEncoder: EntityEncoder[T],
                                           jsonListEncoder: EntityEncoder[List[T]],
                                           jsonDecoder: EntityDecoder[T]) = HttpService {

      case GET -> Root / `basePath` =>
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

  object OperationsResource {
    val basePath = "operations"

    def apply()(implicit modelService: OperationsService,
                jsonEncoder: EntityEncoder[Operation],
                jsonListEncoder: EntityEncoder[List[Operation]],
                jsonDecoder: EntityDecoder[Operation]) = AuthedService[UserProfile] {

      case GET -> Root / `basePath` as user =>
        modelService
          .list(user.id)
          .map(_.toList)
          .flatMap(Ok(_))

      case request@POST -> Root / `basePath` as user =>
        request.req.decode[Operation] { model =>
          modelService
            .create(model.copy(user_id = user.id))
            .flatMap(_ => Created())
        }

      case GET -> Root / `basePath` / IdVar(id) as user =>
        modelService.find(id) flatMap {
          case Some(model@Operation(_,_, _, _, _, _, user.id)) => Ok.apply(model)
          case Some(_) => Forbidden()
          case None => NotFound()
        }

      case DELETE -> Root / `basePath` / IdVar(id) as user =>
        modelService.find(id) flatMap {
          case Some(model@Operation(_, _, _, _, _, _, user.id)) => modelService.delete(model)
            .flatMap(_ => NoContent())
          case Some(_) => Forbidden()
          case None => NotFound()
        }
    }
  }

  object PlacesResource {
    val basePath = "places"

    def apply()(implicit placesService: PlacesService, 
      jsonEncoder: EntityEncoder[Place], 
      jsonListEncoder: EntityEncoder[List[Place]],
      jsonPlaceDecoder: EntityDecoder[PlaceWithCategories]) = AuthedService[UserProfile] {

      case GET -> Root / `basePath` as user =>
        placesService
          .list()
          .map(_.toList)
          .flatMap(Ok(_))

      case request@PUT -> Root / `basePath` as user =>
        request.req.decode[PlaceWithCategories] { model =>
           placesService
            .create(model)
            .flatMap(Ok(_))

        }


    }
  }


  object IdVar {
    def unapply(string: String): Option[Int] =
      Try(string.toInt).toOption
  }

}

