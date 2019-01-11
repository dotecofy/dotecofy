package cloud.dest.sbf.auth.client

import cloud.dest.sbf.auth.client.Authenticate.AuthUser
import cloud.dest.sbf.exception.{Error, ErrorBuilder}
import org.json4s.JsonDSL.WithBigDecimal._
import org.json4s._
import org.specs2.mutable.Specification
import pdi.jwt.{JwtAlgorithm, JwtJson4s}

class TestAuthenticate extends Specification {

  "Authenticate" >> {

    "Should return valid AuthUser" >> {

      val secretKey = "secretKey"

      val sub = "aopjAf7JPawfI3p"
      val name = "Joël Favre"
      val email = "joel.favre@dest.cloud"

      val claim = JObject(("sub", sub), ("name", name), ("email", email))
      val algo = JwtAlgorithm.HS256
      val token = JwtJson4s.encode(claim, secretKey, algo)

      val resp: Either[Error, AuthUser] = Authenticate.authenticate(token)

      resp.right.get must beEqualTo(AuthUser(sub, name, email))
    }

    "Should return error NOT_ALLOWED, AUTH_FAILED" >> {
      val secretKey = "secretKey2"

      val sub = "aopjAf7JPawfI3p"
      val name = "Joël Favre"
      val email = "joel.favre@dest.cloud"

      val claim = JObject(("sub", sub), ("name", name), ("email", email))
      val algo = JwtAlgorithm.HS256
      val token = JwtJson4s.encode(claim, secretKey, algo)

      val resp: Either[Error, AuthUser] = Authenticate.authenticate(token)

      resp.left.get must beEqualTo(Error(ErrorBuilder.NOT_ALLOWED, "AUTH_FAILED", Option("Could not verify the token"), Option(List())))
    }

    "Should not be able to extract the data" >> {
      val secretKey = "secretKey"

      val sub = "aopjAf7JPawfI3p"
      val name = "Joël Favre"
      val email = "joel.favre@dest.cloud"

      val claim = JObject(("sub2", sub), ("name", name), ("email", email))
      val algo = JwtAlgorithm.HS256
      val token = JwtJson4s.encode(claim, secretKey, algo)

      val resp: Either[Error, AuthUser] = Authenticate.authenticate(token)

      resp.left.get must beEqualTo(Error(ErrorBuilder.BAD_REQUEST, "WRONG_FORMAT", Option("Could not extract claims. Available claims are : sub, name, email"), Option(List())))
    }

    "Should return an error (no token)" >> {
      val resp: Either[Error, AuthUser] = Authenticate.authenticate("")

      resp.left.get must beEqualTo(Error(ErrorBuilder.NOT_ALLOWED, "AUTH_FAILED", Option("Could not verify the token"), Option(List())))
    }

  }

}
