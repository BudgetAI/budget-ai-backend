package it.sciencespir.smartbudget.persistence.driver

import it.sciencespir.smartbudget.persistence.query.QueryComponent
import it.sciencespir.smartbudget.persistence.service.ServicesComponent
import it.sciencespir.smartbudget.persistence.table.TablesComponent
import slick.backend.DatabaseConfig
import slick.driver.H2Driver.api
import slick.driver.JdbcProfile
import com.typesafe.scalalogging.StrictLogging
import com.typesafe.config.ConfigFactory

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
    override val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("development", ConfigFactory.load())
  }

  def testDBProvider = new DBConfigProvider {
    override val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("h2mem1")
  }
}
