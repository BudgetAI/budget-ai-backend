package it.sciencespir.smartbudget.DB.model

import argonaut.Argonaut._
import argonaut.{DecodeJson, EncodeJson}
import it.sciencespir.smartbudget.http.Codecs._
import org.joda.time.DateTime

/**
  * Created by kamilbadyla on 14/01/17.
  */
case class Operation(id: Int, amount: Float, currency_iso: String, date: DateTime, category_id: Int, user_id: Int) extends CRUD

object OperationJSON {
  implicit def OperationEncodeJSON: EncodeJson[Operation] =
    jencode6L((p: Operation) => (p.id, p.amount, p.currency_iso, p.date, p.category_id, p.user_id))("id", "amount", "currency_iso", "date", "category_id", "user_id")

  implicit def OperationDecodeJSON: DecodeJson[Operation] =
    DecodeJson(c => for {
      amount <- (c --\ "amount").as[Float]
      currency_iso <- (c --\ "currency_iso").as[String]
      date <- (c --\ "date").as[DateTime]
      category_id <- (c --\ "category_id").as[Int]
      user_id <- (c --\ "user_id").as[Int]
    } yield Operation(0, amount, currency_iso, date, category_id, user_id))
}
