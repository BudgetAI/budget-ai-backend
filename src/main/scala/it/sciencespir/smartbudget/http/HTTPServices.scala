package it.sciencespir.smartbudget.http

import it.sciencespir.smartbudget.DB.model._
import it.sciencespir.smartbudget.DB.model.User._
import it.sciencespir.smartbudget.DB.service.{CategoriesService, OperationsService, UsersService}
import Encoders._
import it.sciencespir.smartbudget.DB.model.CategoryJSON._
import it.sciencespir.smartbudget.DB.model.OperationJSON._
import org.http4s.{AuthedService, HttpService, Request, Service}
import org.http4s.dsl._
import org.http4s.server.syntax._

import scalaz.{Category => _, _}
import scalaz.Scalaz._
import it.sciencespir.smartbudget.DB.validator.Validation._
import APIError._
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

import scalaz.concurrent.Task

/**
  * Created by kamilbadyla on 21/01/17.
  */
object HTTPServices {

  def apply()(implicit categoriesService: CategoriesService, operationsService: OperationsService, usersService: UsersService): HttpService = {
    val filters = (LoggingFilter andThen GZipFilter)
    return filters(CRUDResource[Category]("categories") orElse CRUDResource[Operation]("operations") orElse HTTPLoginService())
  }
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
          user <- User.validateTask(userForm)
          userTaskEither = usersService.create(user).map(_.right)
          u <- EitherT(userTaskEither)
        } yield u
        r.fold(ruleViolationToAPIError(_).toHttpResponse(request.httpVersion), user => Ok(toUserProfile(user))).join
      }

    case request@POST -> Root / "users" / "log_in" =>
      request.decode[UserLoginForm] { userLogin =>
        usersService.login(userLogin).map(_.fold(Forbidden())(Ok(_))).join
      }
  }

}