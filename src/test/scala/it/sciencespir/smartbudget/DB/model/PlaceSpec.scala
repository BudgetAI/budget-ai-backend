package it.sciencespir.smartbudget.DB.model

import org.scalatest.{WordSpec, Matchers}
import java.io.InputStream
import scalaz._, Scalaz._
import argonaut._, Argonaut._
import it.sciencespir.smartbudget.DB.model.PlaceJSON._

class PlaceSpec extends WordSpec with Matchers {
//  "PlaceJSONDecoder" should {
//    "decode test json file" in {
//      val stream: InputStream = getClass.getResourceAsStream("/places.json")
//      val lines = scala.io.Source.fromInputStream(stream).mkString
//      assert(lines.length > 0)
//      val decodeResult = Parse.decodeEither[Places](lines)
//
//      assert(decodeResult.isRight, decodeResult.leftSideValue)
//      assert(decodeResult.right.toOption.get.results.size == 17)
//      val firstPlace = decodeResult.right.toOption.get.results.head
//      assert(firstPlace.icon == "https://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png")
//      assert(firstPlace.name == "Tarnowskie Gory")
//      assert(firstPlace.place_id == "ChIJF26hce4pEUcRlD8cb1rRadA")
//      assert(firstPlace.vicinity == "Tarnowskie Gory")
//      firstPlace.types should contain only ("locality", "political") 
//    }
//
//  }
}
