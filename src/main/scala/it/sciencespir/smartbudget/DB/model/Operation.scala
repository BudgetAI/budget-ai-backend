package it.sciencespir.smartbudget.DB.model

import argonaut.Argonaut.casecodec6
import org.joda.time.DateTime
import it.sciencespir.smartbudget.http.Codecs._

/**
  * Created by kamilbadyla on 14/01/17.
  */
case class Operation(id: Int, amount: Float, currency_iso: String, date: DateTime, category_id: Int, user_id: Int) extends CRUD

object OperationJSON {
  implicit val operationEncodeJson =
    casecodec6(Operation.apply, Operation.unapply)("id", "amount", "currency_iso", "date", "category_id", "user_id")
}
