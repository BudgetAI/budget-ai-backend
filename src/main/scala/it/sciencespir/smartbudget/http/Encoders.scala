package it.sciencespir.smartbudget.http

import argonaut._
import Argonaut._
import it.sciencespir.smartbudget.DB.model._
import org.http4s.argonaut.ArgonautInstances
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
  * Created by kamilbadyla on 21/01/17.
  */


object Codecs {
  implicit val dateTimeCodec = CodecJson[DateTime](
    _.toString.asJson,
    _.as[String].map(DateTimeFormat.fullDateTime().parseDateTime(_)))
}

object Encoders {
  private val argonautInstances = ArgonautInstances.withPrettyParams(PrettyParams.spaces2)

  implicit def modelEncoder[T <: CRUD](implicit encodeJson: EncodeJson[T]) =
    argonautInstances.jsonEncoderOf[T]

  implicit def modelDecoder[T <: CRUD](implicit decodeJson: DecodeJson[T]) =
    argonautInstances.jsonOf[T]

}
