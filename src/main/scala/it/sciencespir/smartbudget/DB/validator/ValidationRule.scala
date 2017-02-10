package it.sciencespir.smartbudget.DB.validator

/**
  * Created by kamilbadyla on 28.01.2017.
  */

import argonaut._
import it.sciencespir.smartbudget.http.APIError
import org.http4s._

import scalaz.syntax.foldable._
import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import org.http4s.EntityEncoder
import org.http4s.argonaut.ArgonautInstances
import org.http4s.dsl._
import it.sciencespir.smartbudget.http.Encoders._
import it.sciencespir.smartbudget.http.APIError._

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
}




