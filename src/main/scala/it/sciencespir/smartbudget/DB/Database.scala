package it.sciencespir.smartbudget.DB

import slick.driver.PostgresDriver.api._

/**
  * Created by kamilbadyla on 20/01/17.
  */
object DB {
  def database() =
    Database.forConfig("mydb")
}
