package it.sciencespir.smartbudget.DB.validator

/**
  * Created by kamilbadyla on 28.01.2017.
  */

import argonaut._
import ArgonautShapeless._
import Argonaut._
import org.http4s._

import scalaz.syntax.foldable._
import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import org.http4s.EntityEncoder
import org.http4s.argonaut.ArgonautInstances
import org.http4s.dsl._

object Validation {
  object dsl {
    implicit class ValidatedValue[T](value: T) {
      def is(rule: T => Boolean): Validation[RuleViolation,T] = {
        if (rule(value)) {
          Success(value)
        }
        else {
          Failure(RuleViolation.unknown)
        }
      }
    }

    implicit class ExtendedValidation[E,V](val value: Validation[E,V]) extends AnyVal {
      def |/[F](other: RuleViolation) = value.leftMap(_ => other)
    }
  }

  case class RuleViolation(reason: String)

  object RuleViolation {
    implicit def toViolation(reason: String): RuleViolation = {
      return RuleViolation(reason)
    }
    val unknown = RuleViolation("Unknown violation")
  }

  implicit val ruleViolationCoder = EncodeJson.of[List[RuleViolation]]
  implicit val ruleViolationEntityCoder = ArgonautInstances.withPrettyParams(PrettyParams.spaces2).jsonEncoderOf[List[RuleViolation]](ruleViolationCoder)

  class MessageFailureValidation[T](violations: NonEmptyList[RuleViolation]) {
    def toMessageFailure(): MessageFailure =
      GenericMessageBodyFailure("Message didn't pass validation", None, httpVersion => ruleViolationEntityCoder.toEntity(violations.list.toList).map(entity => Response(Status.BadRequest, httpVersion, body = entity.body)))
  }

  implicit def toMsgFailureValidation[T](violations: NonEmptyList[RuleViolation]) = new MessageFailureValidation(violations)

}




