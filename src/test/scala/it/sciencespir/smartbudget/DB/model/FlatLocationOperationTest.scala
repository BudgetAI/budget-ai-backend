package it.sciencespir.smartbudget.DB.model

import org.joda.time.DateTime
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, FunSuite, Matchers}

/**
  * Created by kamilbadyla on 08.04.2017.
  */
class FlatLocationOperationTest extends FlatSpec with GeneratorDrivenPropertyChecks with Matchers {

  "FlatLocationOperator" should "create Operation" in {
    forAll { (id: Int, amount: Float, latitude: Option[Float], longitude: Option[Float], category: Int, user: Int) =>
      val operation = FlatLocationOperation(id, amount, DateTime.now(), latitude, longitude, category, user)
      operation.user_id shouldBe user
      operation.id shouldBe id
      operation.amount shouldBe amount
      operation.category_id shouldBe category
      if (latitude.isEmpty || longitude.isEmpty) {
        operation.location.isEmpty shouldBe true
      }
      else {
        operation.location.isDefined shouldBe true
        operation.location.get.latitude shouldBe latitude.get
        operation.location.get.longitude shouldBe longitude.get
      }
    }
  }

}
