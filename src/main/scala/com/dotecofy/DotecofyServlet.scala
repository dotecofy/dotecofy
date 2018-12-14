package com.dotecofy

import org.scalatra._

import scala.concurrent._
import ExecutionContext.Implicits.global

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

import com.dotecofy.workspace.feature._
/*class DotecofyServlet extends ScalatraServlet {

  get("/") {
    views.html.hello()
  }

}*/

class DotecofyServlet extends ScalatraServlet with FutureSupport with DotecofyRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }
}
