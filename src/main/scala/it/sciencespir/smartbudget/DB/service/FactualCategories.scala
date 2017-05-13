package it.sciencespir.smartbudget.DB.service

import org.http4s.dsl._
import it.sciencespir.smartbudget.DB.model.FactualCategory
import argonaut._, argonaut.Argonaut._
import scalaz._, Scalaz._
import scala.collection.immutable._
import scalaz.concurrent.Task
import org.http4s.Uri
import com.typesafe.config.ConfigFactory
import org.http4s.client.blaze._

case class FactualCategoriesAPI(data: List[FactualCategory])

object FactualCategories {
  import it.sciencespir.smartbudget.http.Encoders._

  implicit val categoryDecoder: DecodeJson[FactualCategory] =
    DecodeJson(c ⇒ for {
      id ← (c --\ "id").as[Int]
      name ← (c --\ "label").as[String]
      parent_id ← (c --\ "parents" =\ 0).as[Option[Int]]
    } yield FactualCategory(id, parent_id, name))

  implicit val categoriesDecoder: DecodeJson[FactualCategoriesAPI] =
    DecodeJson(c ⇒ for {
      data ← (c --\ "response" --\ "data").as[Map[String, FactualCategory]]
    } yield FactualCategoriesAPI(data.map { case (k, v) ⇒ v }.toList))

  def apiCategories: Task[FactualCategoriesAPI] = {
    val httpClient = PooledHttp1Client()
    httpClient.expect[FactualCategoriesAPI](factualCategoriesURI)
  }

  def factualCategoriesURI: Uri =
    uri("https://api.factual.com") / "categories" +? ("options", "{\"lang\":\"en\", \"format\":\"index\"}") +? ("KEY", ConfigFactory.load().getString("factualKey"))
}
