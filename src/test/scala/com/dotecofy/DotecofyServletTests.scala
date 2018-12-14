package com.dotecofy

import org.scalatra.test.scalatest._

class DotecofyServletTests extends ScalatraFunSuite {

  addServlet(classOf[DotecofyServlet], "/*")

  test("GET / on DotecofyServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
