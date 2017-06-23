package it.sciencespir.smartbudget.persistence.model

import argonaut.Argonaut._
import argonaut._
import ArgonautShapeless._
import com.github.t3hnar.bcrypt._
import it.sciencespir.smartbudget.persistence.validator.Validation.RuleViolation

import scalaz._
import Scalaz._
import it.sciencespir.smartbudget.persistence.validator.Validation.dsl._
import uk.gov.hmrc.emailaddress.EmailAddress

import scalaz.concurrent.Task
import authentikat.jwt._
import com.typesafe.config.ConfigFactory


/**
  * Created by kamilbadyla on 14/01/17.
  */

case class EncryptedPassword(hash: String, salt: String) {
  def validate(password: String): Boolean = password.isBcrypted(hash)
}

object EncryptedPassword {
  implicit def encodePassword(password: String): EncryptedPassword = {
    val salt = generateSalt
    return EncryptedPassword(password.bcrypt(salt), salt)
  }
}

object User {
  implicit val userProfileEncodeJson = EncodeJson.of[UserProfile]
  implicit val userProfileDecodeJson = DecodeJson.of[UserProfile]
  implicit val userFormDecodeJson = DecodeJson.of[UserForm]
  implicit val userLoginDecodeJson = DecodeJson.of[UserLoginForm]
  implicit val userAuthEncodeJson = EncodeJson.of[UserAuth]

  implicit def toUserProfile(user: User) = UserProfile(user.id, user.name, user.email)

  def validName(name: String): Boolean = {
    return name.length > 0
  }

  def validPassword(password: String): Boolean = {
    return password.length > 0
  }

  def create(name: String, email: String, password: String) = {
    import EncryptedPassword._
    val result = ((
      (name is validName) |/ "Name is too short").toValidationNel |@|
      ((email is EmailAddress.isValid) |/ "Email is not valid").toValidationNel |@|
      ((password is validPassword) |/ "Password is too short").toValidationNel) { (n, e, p) =>
      val encryptedPassword: EncryptedPassword = p
      User(0, n, e, encryptedPassword.hash, encryptedPassword.salt)
    }
    result
  }

  def validateTask(userForm: UserForm): EitherT[Task, NonEmptyList[RuleViolation], User] = EitherT(Task.now((User.create _).tupled(UserForm.unapply(userForm).get).disjunction))
}

object UserAuth {
  val secretKey = ConfigFactory.load().getString("jwtSecretKey")

  def apply(user: UserProfile): UserAuth = {
    val header = JwtHeader("HS256")
    val claimsSet = JwtClaimsSet(user.asJson.nospaces)
    val jwt: String = JsonWebToken(header, claimsSet, secretKey)
    UserAuth(jwt)

  }

  def userProfile(jwt: String): Option[UserProfile] =  jwt match {
    case JsonWebToken(header, claimsSet, signature) =>
      claimsSet.asJsonString.decodeOption[UserProfile]
    case x =>
      None
    }

}

trait Profile {
  val name: String
  val email: String
}

case class User private (id: Int, name: String, email: String, password_hash: String, salt: String) extends CRUD with Profile
case class UserProfile (id: Int, name: String, email: String) extends Profile

case class UserForm (name: String, email: String, password: String) extends Profile
case class UserLoginForm(email: String, password: String)
case class UserAuth private (jwt: String)
