package it.sciencespir.smartbudget.http

import argonaut._
import Argonaut._
import it.sciencespir.smartbudget.persistence.model._
import org.http4s.EntityEncoder
import org.http4s.argonaut.ArgonautInstances
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, ISODateTimeFormat}

/**
  * Created by kamilbadyla on 21/01/17.
  */


object Codecs {
  implicit val dateTimeCodec = CodecJson[DateTime](
    _.toString.asJson,
    _.as[String].map(ISODateTimeFormat.dateTimeParser().parseDateTime(_)))
}

object Encoders {
  private val argonautInstances = ArgonautInstances.withPrettyParams(PrettyParams.spaces2)

  implicit def modelEncoder[T](implicit encodeJson: EncodeJson[T]): EntityEncoder[T] =
    argonautInstances.jsonEncoderOf[T]

  implicit def modelListEncoder[T](implicit encodeJson: EncodeJson[List[T]]): EntityEncoder[List[T]] =
    argonautInstances.jsonEncoderOf[List[T]]

  implicit def modelDecoder[T](implicit decodeJson: DecodeJson[T]) =
    argonautInstances.jsonOf[T]

}
