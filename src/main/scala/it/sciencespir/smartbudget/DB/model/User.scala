package it.sciencespir.smartbudget.DB.model

import java.util.UUID

import argonaut.Argonaut.casecodec2

/**
  * Created by kamilbadyla on 14/01/17.
  */

case class User (val id: Int, val name: String) extends CRUD

object UserJSON {
  implicit val userEncodeJson =
    casecodec2(User.apply, User.unapply)("id", "name")
}