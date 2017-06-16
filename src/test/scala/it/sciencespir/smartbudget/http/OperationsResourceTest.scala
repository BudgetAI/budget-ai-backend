package it.sciencespir.smartbudget.http

import it.sciencespir.smartbudget.DB.model._
import it.sciencespir.smartbudget.DB.model.OperationJSON._
import org.http4s._
import org.scalatest._
import org.http4s.dsl._
import Encoders._
import it.sciencespir.smartbudget.DB.driver.{DB, TestDatabaseComponent}
import org.joda.time.DateTime
import slick.driver.PostgresDriver

import scalaz.concurrent.Task


/**
  * Created by kamilbadyla on 08.04.2017.
  */
class OperationsResourceTest extends WordSpec with BeforeAndAfter {

  object TestHttpService extends HTTPServices with TestDatabaseComponent {
    implicit val modelService = this.operationsService
    val operationsHTTPService = OperationsResource()
  }
  before {
    TestHttpService.usersService.initializeIfNeeded().unsafePerformSync
    TestHttpService.categoriesService.initializeIfNeeded().unsafePerformSync
    TestHttpService.placesService.initializeIfNeeded().unsafePerformSync
    TestHttpService.operationsService.initializeIfNeeded().unsafePerformSync


    TestHttpService.categoriesService.create(Category(1,"test", None)).unsafePerformSync
    TestHttpService.usersService.create(User(1, "test", "test123@gmail.com", "", "")).unsafePerformSync
    TestHttpService.usersService.create(User(2, "test", "test124@gmail.com", "", "")).unsafePerformSync
    TestHttpService.operationsService.create(Operation(1, 0.1f, DateTime.now(), None, None, 1, 1)).unsafePerformSync
    TestHttpService.operationsService.create(Operation(2, 0.1f, DateTime.now(), None, None, 1, 2)).unsafePerformSync
  }

  "Operations request" should {
    "returns operations for logged user" when {
      "user is defined" in {
        val listRequest = Request(GET, uri("http://localhost/operations"))
        val userProfile = UserProfile(1, "", "")

        val request = AuthedRequest(userProfile, listRequest)
        val taskResponse = TestHttpService.operationsHTTPService(request).flatMap(_.as[List[Operation]])
        val resp = taskResponse.unsafePerformSync
        assert(resp.length == 1)
      }
    }
  }

}
