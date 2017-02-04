package it.sciencespir.smartbudget.DB.table

import it.sciencespir.smartbudget.DB.model.{Category, Operation, User}
import org.joda.time.DateTime
import it.sciencespir.smartbudget.DB.driver.PGDriver.api._
import it.sciencespir.smartbudget.DB.query._
import slick.lifted.ForeignKeyQuery


/**
  * Created by kamilbadyla on 14/01/17.
  */

abstract class CRUDTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
}

class Users(tag: Tag) extends CRUDTable[User](tag, "Users") {
  def name = column[String]("name")
  def email = column[String]("email")
  def password_hash = column[String]("password_hash")
  def password_salt = column[String]("password_salt")
  def * = (id, name, email, password_hash, password_salt) <> ((User.apply _).tupled, User.unapply)
}

class Operations(tag: Tag) extends CRUDTable[Operation](tag, "Operations") {
  def amount = column[Float]("amount")
  def currency_iso = column[String]("currency_iso")
  def date = column[DateTime]("date")
  def category_id = column[Int]("category_id")
  def user_id = column[Int]("user_id")

  def * = (id, amount, currency_iso, date, category_id, user_id) <> (Operation.tupled, Operation.unapply)


  def category = foreignKey("CAT_FK", category_id, categories)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  def user = foreignKey("USER_FK", user_id, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

}

class Categories(tag: Tag) extends CRUDTable[Category](tag, "Categories") {
  def name = column[String]("name")
  def creator_user_id = column[Option[Int]]("creator_user_id")
  def * = (id, name, creator_user_id) <> (Category.tupled, Category.unapply)

  def user = foreignKey("CREATOR_FK", creator_user_id, users)(_.id)
}