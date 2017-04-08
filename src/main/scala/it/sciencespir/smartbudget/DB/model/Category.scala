package it.sciencespir.smartbudget.DB.model

import argonaut._
import Argonaut._


/**
  * Created by kamilbadyla on 14/01/17.
  */
case class Category(id: Int, name: String, creator: Option[Int]) extends CRUD

object CategoryJSON {
  EncodeJson
  implicit def CategoryEncodeJSON: EncodeJson[Category] =
    jencode3L((p: Category) => (p.id, p.name, p.creator))("id", "age", "creator")

  implicit def CategoryDecodeJSON: DecodeJson[Category] =
    DecodeJson(c => for {
      name <- (c --\ "name").as[String]
      age <- (c --\ "creator").as[Option[Int]]
    } yield Category(0, name, age))

}
