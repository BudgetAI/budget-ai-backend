package it.sciencespir.smartbudget.http

import argonaut._
import Argonaut._
import ArgonautShapeless._
import it.sciencespir.smartbudget.DB.validator.Validation.RuleViolation
import it.sciencespir.smartbudget.http.Encoders.modelEncoder
import org.http4s.{GenericMessageBodyFailure, Response, Status}
import org.http4s.argonaut.ArgonautInstances

import scalaz.NonEmptyList

/**
  * Created by kamilbadyla on 10.02.2017.
  */

object APIErrorTypes {
  sealed abstract class Type(
    val name: String,
    val code: Int
  )
  case object Validation extends Type("Validation Error", 100)
}

case class APIError(errorType: APIErrorTypes.Type, reasons: List[APIErrorReason])
case class APIErrorReason(message: String)

object APIError {
  implicit def ruleViolationToReason(rules: List[RuleViolation]) = rules.map(rule => APIErrorReason(rule.reason))
  implicit def ruleViolationToAPIError(rules: List[RuleViolation]) = APIError(APIErrorTypes.Validation, rules)
  implicit def ruleViolationToAPIError(rules: NonEmptyList[RuleViolation]) = APIError(APIErrorTypes.Validation, rules.list.toList)

  implicit def apiErrorReasonsToJson = EncodeJson.of[APIErrorReason]
  implicit def apiErrorToJSON: EncodeJson[APIError] = jencode3L((e: APIError) => (e.errorType.name, e.errorType.code, e.reasons))("name", "code", "reasons")

  implicit def toMessageFailure(apiError: APIError) = {
    GenericMessageBodyFailure(apiError.errorType.name, None, httpVersion =>
      modelEncoder(apiErrorToJSON).toEntity(apiError).map(entity =>
        Response(Status.BadRequest, httpVersion, body = entity.body)
      )
    )
  }

}
