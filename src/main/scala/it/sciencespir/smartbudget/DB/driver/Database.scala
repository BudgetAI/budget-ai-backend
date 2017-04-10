package it.sciencespir.smartbudget.DB.driver

import it.sciencespir.smartbudget.DB.query.QueryComponent
import it.sciencespir.smartbudget.DB.service.ServicesComponent
import it.sciencespir.smartbudget.DB.table.TablesComponent
import slick.backend.DatabaseConfig
import slick.driver.H2Driver.api
import slick.driver.JdbcProfile

/**
  * Created by kamilbadyla on 20/01/17.
  */

trait DatabaseComponent extends ServicesComponent with TablesComponent with QueryComponent with DBConfigProvider {

}

trait DevDatabaseComponent extends DatabaseComponent with DevelopmentDBConfig {

}

trait TestDatabaseComponent extends DatabaseComponent with TestDBConfig {

}

object DB {
  def developmentDBProvider = new DBConfigProvider {
    override val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("development")
  }

  def testDBProvider = new DBConfigProvider {
    override val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("h2mem1")
  }
}
