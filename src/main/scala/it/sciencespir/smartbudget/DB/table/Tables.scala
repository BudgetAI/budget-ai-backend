package it.sciencespir.smartbudget.DB.table

import it.sciencespir.smartbudget.DB.driver.DBConfigProvider
import it.sciencespir.smartbudget.DB.model.{Category, FlatLocationOperation, Operation, User}
import it.sciencespir.smartbudget.DB.query.QueryComponent
import org.joda.time.DateTime
import slick.driver.PostgresDriver.api._

/**
  * Created by kamilbadyla on 14/01/17.
  */

trait TablesComponent {
  this: QueryComponent with DBConfigProvider =>

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

  class Operations(tag: Tag) extends CRUDTable[Operation](tag, "Operations") {
    def amount = column[Float]("amount")

    def date = column[DateTime]("date")

    def latitude = column[Option[Float]]("latitude")

    def longitude = column[Option[Float]]("longitude")

    def category_id = column[Int]("category_id")

    def user_id = column[Int]("user_id")

    def * = (id, amount, date, latitude, longitude, category_id, user_id) <> ((FlatLocationOperation.apply _).tupled, FlatLocationOperation.unapply)


    def category = foreignKey("CAT_FK", category_id, categories)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def user = foreignKey("USER_FK", user_id, users)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  }

  class Categories(tag: Tag) extends CRUDTable[Category](tag, "Categories") {
    def name = column[String]("name")

    def creator_user_id = column[Option[Int]]("creator_user_id")

    def * = (id, name, creator_user_id) <> (Category.tupled, Category.unapply)

    def user = foreignKey("CREATOR_FK", creator_user_id, users)(_.id)
  }

}


