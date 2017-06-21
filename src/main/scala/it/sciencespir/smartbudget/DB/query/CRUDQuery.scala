package it.sciencespir.smartbudget.DB.query

import it.sciencespir.smartbudget.DB.driver.DBConfigProvider
import it.sciencespir.smartbudget.DB.table._
import it.sciencespir.smartbudget.DB.model._
import slick.dbio.DBIOAction
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext
import scala.util.{Success, Try}
import scala.util.Failure


/**
  * Created by kamilbadyla on 20/01/17.
  */

trait QueryComponent {
  this: TablesComponent with DBConfigProvider =>

  import dbConfig.driver.api._

  abstract class DBTQuery[M, MT <: Table[M]](cons: Tag => MT) extends TableQuery[MT](cons) {

    def initialize() =
      this.schema.create

    def initializeIfNeeded(implicit executionContext: ExecutionContext) =
      MTable
        .getTables
        .map(_.exists(_.name.name == this.shaped.value.tableName))
        .asTry
        .flatMap {
          case Success(false) => this.initialize()
          case Success(true) => DBIOAction.successful()
          case Failure(e) => DBIOAction.failed(e)
        }

    def list() =
      this.result

    def insert(model: M) =
      this += model


  }

  abstract class CRUDQuery[M <: CRUD, MT <: CRUDTable[M]](cons: Tag => MT) extends DBTQuery[M, MT](cons) {

    def find(id: Int) =
      filter(_.id === id).result

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

    def withPlaces = 
      this.join(places)
  }

  object operations extends OperationsCRUDQuery

  class PlacesCRUDQuery extends DBTQuery[Place, Places](new Places(_)) {
    
  }

  object places extends PlacesCRUDQuery

  class FactualCategoriesCRUDQuery extends CRUDQuery[FactualCategory,FactualCategories ](new FactualCategories(_)) {

  }

  object factualCategories extends FactualCategoriesCRUDQuery

  class PlaceFactualCategoriesCRUDQuery extends DBTQuery[PlaceFactualCategory,PlaceFactualCategories ](new PlaceFactualCategories(_)) {

  }

  object placeFactualCategories extends PlaceFactualCategoriesCRUDQuery

}
