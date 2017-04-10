package it.sciencespir.smartbudget.DB.model

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import it.sciencespir.smartbudget.http.Codecs._
import org.joda.time.DateTime

/**
  * Created by kamilbadyla on 14/01/17.
  */

case class GeoLocation(latitude: Float, longitude: Float)

object GeoLocationJSON {
  implicit val geoLocationCodec = casecodec2(GeoLocation.apply, GeoLocation.unapply)("latitude", "longitude")
}

case class Operation(id: Int, amount: Float, date: DateTime, location: Option[GeoLocation], category_id: Int, user_id: Int) extends CRUD

object FlatLocationOperation {
  def apply(id: Int, amount: Float, date: DateTime, latitude: Option[Float], longitude: Option[Float], category_id: Int, user_id: Int): Operation = {
    val geoLocation = for {
      lat <- latitude
      long <- longitude
    } yield GeoLocation(lat, long)

    Operation(id, amount, date, geoLocation, category_id, user_id)
  }

  def unapply(arg: Operation): Option[(Int, Float, DateTime, Option[Float], Option[Float], Int, Int)] = {
    Some(arg.id, arg.amount, arg.date, arg.location.map(_.latitude), arg.location.map(_.longitude), arg.category_id, arg.user_id)
  }

}

object OperationJSON {
  import GeoLocationJSON._

  implicit def OperationEncodeJSON: EncodeJson[Operation] =
    jencode6L((p: Operation) => (p.id, p.amount, p.date, p.location, p.category_id, p.user_id))("id", "amount", "date", "location", "category_id", "user_id")

  implicit def OperationDecodeJSON: DecodeJson[Operation] =
    DecodeJson(c => for {
      amount <- (c --\ "amount").as[Float]
      date <- (c --\ "date").as[DateTime]
      location <- (c --\ "location").as[Option[GeoLocation]]
      category_id <- (c --\ "category_id").as[Int]
    } yield Operation(0, amount, date, location, category_id, 0))
}
