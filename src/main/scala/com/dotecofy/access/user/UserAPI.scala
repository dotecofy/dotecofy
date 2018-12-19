package com.dotecofy.access.user

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport

trait UserRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  get("/") {
    //FeatureServices.load(0, 50)
  }
}

class UserAPI extends ScalatraServlet with FutureSupport with UserRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
}