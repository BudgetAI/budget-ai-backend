package it.sciencespir.smartbudget.persistence.model

/**
  * Created by kamilbadyla on 20/01/17.
  */
trait CRUD {
  val id: Int
}

trait AuthCRUD extends CRUD {
  val user_id: Int
}
