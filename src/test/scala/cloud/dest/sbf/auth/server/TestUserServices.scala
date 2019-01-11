package cloud.dest.sbf.auth.server

import cloud.dest.sbf.auth.server.user.{UserDB, UserServices}
import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.Specification

import scala.language.experimental.macros

class TestUserServices extends Specification with MatcherMacros {


  "Test new user" >> {

    "Should insert correctly" >> {

      val userDB = UserDB(
        fullName = "Joël Favre",
        email = "joel.favre@dest.cloud",
        password = "Pa$$1337"
      )

      //UserServices.newUser(userDB).right.get must matchA[UserDB].email("joel.favre@dest.cloud")

      UserServices.newUser(userDB) must beLike {
        case Right(resp) => resp.email must beEqualTo(userDB.email)
      }
    }

    "Test fullname" >> {

      UserServices.newUser(UserDB(fullName = null, email = "test@example.com", password = "password")) must beLike {
        case Left(error) => (error.details.get.head.code must beEqualTo("fullname")) and (error.details.get(0).message.get must beEqualTo("is_null"))
      }

      UserServices.newUser(UserDB(fullName = "ab", email = "test@example.com", password = "password")) must beLike {
        case Left(error) => (error.details.get.head.code must beEqualTo("fullname")) and (error.details.get(0).message.get must beEqualTo("fullname_too_short"))
      }

      UserServices.newUser(UserDB(fullName = (scala.util.Random.alphanumeric take UserServices.FULLNAME_MAX_LENGTH+1).mkString, email = "test@example.com", password = "password")) must beLike {
        case Left(error) => (error.details.get.head.code must beEqualTo("fullname")) and (error.details.get(0).message.get must beEqualTo("fullname_too_long"))
      }

    }

    "Test email" >> {

      UserServices.newUser(UserDB( fullName="full name",email = null, password = "password")) must beLike {
        case Left(error) => (error.details.get.head.code must beEqualTo("email")) and (error.details.get(0).message.get must beEqualTo("is_null"))
      }

      UserServices.newUser(UserDB(fullName = "full name",email = "ab",  password = "password")) must beLike {
        case Left(error) => (error.details.get.head.code must beEqualTo("email")) and (error.details.get(0).message.get must beEqualTo("email_too_short"))
      }

      UserServices.newUser(UserDB(fullName ="full name",email = (scala.util.Random.alphanumeric take UserServices.EMAIL_MAX_LENGTH+1).mkString,  password = "password")) must beLike {
        case Left(error) => (error.details.get.head.code must beEqualTo("email")) and (error.details.get(0).message.get must beEqualTo("email_too_long"))
      }

    }

  }

}
