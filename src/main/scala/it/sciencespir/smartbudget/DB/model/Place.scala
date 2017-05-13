package it.sciencespir.smartbudget.DB.model

import scala.collection.immutable._
import argonaut.Argonaut._
import argonaut._
import scalaz._, Scalaz._

case class FactualCategory(id: Int, parent_id: Option[Int], name: String) extends CRUD

case class PlaceFactualCategory(place_id: String, place_type_id: Int) 

case class Place(location: GeoLocation, name: String, factual_place_id: String) 
case class PlaceWithCategories(place: Place, categories: List[Int])

object FlatLocationPlace {
  def apply(latitude: Float, longitude: Float, name: String, place_id: String): Place = {
    Place(GeoLocation(latitude, longitude), name, place_id)
  }

  def unapply(arg: Place): Option[(Float, Float, String, String)] = {
    Some(arg.location.latitude, arg.location.longitude, arg.name, arg.factual_place_id)
  }
}

case class Places(results: List[Place], status: String)


object PlaceJSON {
  import it.sciencespir.smartbudget.DB.model.GeoLocationJSON._

  implicit val placeJSONDecoder: DecodeJson[Place] =
    DecodeJson(c ⇒ for {
      location ← (c --\ "geometry" --\ "location").as[GeoLocation]
      name ← (c --\ "name").as[String]
      place_id ← (c --\ "place_id").as[String]
    } yield Place(location, name, place_id))

  implicit val placeWithCategoriesJSONDecoder: DecodeJson[PlaceWithCategories] = 
    jdecode2L(PlaceWithCategories.apply)("place", "categories")

  implicit val placeJSONEncoder: EncodeJson[Place] = jencode3L((p: Place) ⇒ (p.location, p.name, p.factual_place_id))("location", "name", "place_id")
 
  implicit val placesJSONDecoder = jdecode2L(Places.apply)("results", "status")
}

