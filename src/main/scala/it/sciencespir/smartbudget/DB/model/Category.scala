package it.sciencespir.smartbudget.DB.model

import argonaut.Argonaut.casecodec3
import argonaut.EncodeJson


/**
  * Created by kamilbadyla on 14/01/17.
  */
case class Category(id: Int, name: String, creator: Option[Int]) extends CRUD

object CategoryJSON {
  implicit val categoryEncodeJson =
    casecodec3(Category.apply, Category.unapply)("id", "name", "creator_user_id")
}
