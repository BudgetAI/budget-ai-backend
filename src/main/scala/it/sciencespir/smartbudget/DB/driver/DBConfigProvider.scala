package it.sciencespir.smartbudget.DB.driver

import java.util.concurrent.ExecutorService

import com.github.tototoshi.slick.GenericJodaSupport
import it.sciencespir.smartbudget.util.Executor
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import com.typesafe.scalalogging.StrictLogging
import com.typesafe.config.ConfigFactory

/**
  * Created by kamilbadyla on 10.04.2017.
  */

trait DBConfigProvider extends StrictLogging {
  val dbConfig: DatabaseConfig[JdbcProfile]
  protected implicit lazy val db = { 
    logger.info("DB Config " + dbConfig.config.toString)
    logger.info("POSTGRES URL ENV" + System.getenv("POSTGRES_URL_ENV"))
    dbConfig.db
  }
  protected implicit lazy val executorService = Executor()

  object portableJodaSupport extends GenericJodaSupport(dbConfig.driver)
}

trait DevelopmentDBConfig extends DBConfigProvider {
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("development", ConfigFactory.load())
}

trait TestDBConfig extends DBConfigProvider {
  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("test")
}
