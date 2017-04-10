package it.sciencespir.smartbudget.DB.query

import it.sciencespir.smartbudget.DB.driver.DBConfigProvider
import it.sciencespir.smartbudget.DB.table._
import it.sciencespir.smartbudget.DB.model._
import slick.dbio.DBIOAction
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext
import scala.util.{Success, Try}


/**
  * Created by kamilbadyla on 20/01/17.
  */

trait QueryComponent {
  this: TablesComponent with DBConfigProvider =>

  import dbConfig.driver.api._

  abstract class CRUDQuery[M <: CRUD, MT <: CRUDTable[M]](cons: Tag => MT) extends TableQuery[MT](cons) {


    def initialize() =
      this.schema.create

    def initializeIfNeeded(implicit executionContext: ExecutionContext) =
      MTable
        .getTables
        .filter(!_.exists(_.name.name == this.shaped.value.tableName))
        .asTry
        .flatMap {
          case Success(_) => this.initialize()
          case x => DBIOAction.successful()
        }


    def list() =
      this.result

    def find(id: Int) =
      filter(_.id === id).result

    def insert(model: M) =
      this += model

    def update(model: M) =
      filter(_.id === model.id).update(model)

    def delete(model: M) =
      filter(_.id === model.id).delete
  }

  class CategoriesCRUDQuery extends CRUDQuery[Category, Categories](new Categories(_)) {}

  object categories extends CategoriesCRUDQuery

  class UsersCRUDQuery extends CRUDQuery[User, Users](new Users(_)) {
    def find(email: String) =
      filter(_.email === email).result
  }

  object users extends UsersCRUDQuery

  class OperationsCRUDQuery extends CRUDQuery[Operation, Operations](new Operations(_)) {
    def list(userId: Int) =
      this.filter(_.user_id === userId).result
  }

  object operations extends OperationsCRUDQuery

}