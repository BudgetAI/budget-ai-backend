package it.sciencespir.smartbudget.DB.model

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, FunSuite, Matchers}
import org.typelevel.scalatest.DisjunctionMatchers
import com.github.t3hnar.bcrypt._

/**
  * Created by kamilbadyla on 28.01.2017.
  */
class UserTest extends FlatSpec with GeneratorDrivenPropertyChecks with Matchers with DisjunctionMatchers {

  "encodePassword" should "should hash password" in {
    forAll { (password: String) =>
      whenever(password.length > 0) {
        EncryptedPassword.encodePassword(password).validate(password) shouldBe true
      }
    }
  }

  "user create method" should "validates name and password field" in {
    forAll { (name: String, password: String) =>
      whenever(name.length > 0 && password.length > 0) {
        val email = "validmail@gmail.com"
        val user = User.create(name, email, password)

        user.map(_.name).disjunction should beRight[String](name)
        user.map(_.email).disjunction should beRight[String](email)
        user.map(u => password.isBcrypted(u.password_hash)).disjunction should beRight[Boolean](true)
      }
    }
  }


}
