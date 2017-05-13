package it.sciencespir.smartbudget.DB.table

import it.sciencespir.smartbudget.DB.driver.DBConfigProvider
import it.sciencespir.smartbudget.DB.model.{Category, FlatLocationOperation, Operation, User}
import it.sciencespir.smartbudget.DB.query.QueryComponent
import org.joda.time.DateTime
import it.sciencespir.smartbudget.DB.driver.PGDriver.api._
import it.sciencespir.smartbudget.DB.model._

/**
 * Created by kamilbadyla on 14/01/17.
 */

trait TablesComponent {
  this: QueryComponent with DBConfigProvider ⇒

  import dbConfig.driver.api._
  import portableJodaSupport._

  abstract class CRUDTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  }

  class Users(tag: Tag) extends CRUDTable[User](tag, "Users") {
    def name = column[String]("name")

    def email = column[String]("email")

    def password_hash = column[String]("password_hash")

    def password_salt = column[String]("password_salt")

    def * = (id, name, email, password_hash, password_salt) <> ((User.apply _).tupled, User.unapply)

    def email_index = index("email_index", email, unique = true)
  }

  class Places(tag: Tag) extends Table[Place](tag, "Places") {
    def latitude = column[Float]("latitude")
    
    def longitude = column[Float]("longitude")
    
    def name = column[String]("name")
    
    def factual_place_id = column[String]("factual_place_id", O.PrimaryKey)

    def * = (latitude, longitude, name, factual_place_id) <> ((FlatLocationPlace.apply _).tupled, FlatLocationPlace.unapply)
  }

  class FactualCategories(tag: Tag) extends CRUDTable[FactualCategory](tag, "FactualCategories") {
    def name = column[String]("name")

    def parent_id = column[Option[Int]]("parent_category_id")

    def * = (id, parent_id, name) <> ((FactualCategory.apply _).tupled, FactualCategory.unapply)

    def parent_id_fk = foreignKey("parent_id_fk", parent_id, factualCategories)(_.id)
  }

  class PlaceFactualCategories(tag: Tag) extends CRUDTable[PlaceFactualCategory](tag, "PlaceFactualCategories") {
    def factual_place_id = column[String]("place_id")

    def factual_place_type_id = column[Int]("place_type_id")

    def * = (factual_place_id, factual_place_type_id) <> ((PlaceFactualCategory.apply _).tupled, PlaceFactualCategory.unapply)

    def place = foreignKey("TYPE_PLACE_FK", factual_place_id, places)(_.factual_place_id)

    def place_type = foreignKey("PLACE_TYPE_FK", factual_place_type_id, factualCategories)(_.id)

    def idx = primaryKey("pk", (factual_place_id, factual_place_type_id))
  }

  class Operations(tag: Tag) extends CRUDTable[Operation](tag, "Operations") {
    def amount = column[Float]("amount")

    def date = column[DateTime]("date")

    def latitude = column[Option[Float]]("latitude")

    def longitude = column[Option[Float]]("longitude")

    def category_id = column[Int]("category_id")

    def user_id = column[Int]("user_id")

    def place_id = column[Option[String]]("place_id")

    def * = (id, amount, date, latitude, longitude, place_id, category_id, user_id) <> ((FlatLocationOperation.apply _).tupled, FlatLocationOperation.unapply)

    def category = foreignKey("CAT_FK", category_id, categories)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def user = foreignKey("USER_FK", user_id, users)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def place = foreignKey("PLACE_FK", place_id, places)(_.factual_place_id)

  }

  class Categories(tag: Tag) extends CRUDTable[Category](tag, "Categories") {
    def name = column[String]("name")

    def creator_user_id = column[Option[Int]]("creator_user_id")

    def * = (id, name, creator_user_id) <> (Category.tupled, Category.unapply)

    def user = foreignKey("CREATOR_FK", creator_user_id, users)(_.id)
  }

}

