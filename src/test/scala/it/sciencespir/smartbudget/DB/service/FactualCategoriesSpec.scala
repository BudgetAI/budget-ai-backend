package it.sciencespir.smartbudget.DB.service

import org.scalatest._
import Matchers._
import Inspectors._
import argonaut.Parse
import java.io.InputStream
import it.sciencespir.smartbudget.DB.service.FactualCategories._

class FactualCategoriesSpec extends WordSpec with Matchers {
  "FactualCategories json decoder" should {
    "decode test json file" in {
      val stream: InputStream = getClass.getResourceAsStream("/categories.json")
      val lines = scala.io.Source.fromInputStream(stream).mkString
      assert(lines.length > 0)
      val decodeResult = Parse.decodeEither[FactualCategoriesAPI](lines)

      decodeResult.isRight shouldBe true
      val data = decodeResult.right.toOption.get.data
      data should have size 476
      forAll(data) { d => d.name.length should be > 0 }
      forAll(data) { d => d.id should be > 0 }

    }

  }
}
