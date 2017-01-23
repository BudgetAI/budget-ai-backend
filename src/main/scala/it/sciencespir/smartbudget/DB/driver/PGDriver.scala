package it.sciencespir.smartbudget.DB.driver

/**
  * Created by kamilbadyla on 20/01/17.
  */
import com.github.tminglei.slickpg._
import slick.driver.JdbcProfile
import slick.profile.Capability

trait PGDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDateSupport
  with PgDateSupportJoda
  with PgRangeSupport
  with PgHStoreSupport
  with PgSearchSupport
  with PgNetSupport
  with PgLTreeSupport {
  def pgjson = "jsonb" // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcProfile.capabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API with ArrayImplicits
    with DateTimeImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with SearchImplicits
    with SearchAssistants {

  }
}

object PGDriver extends PGDriver