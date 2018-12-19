package com.dotecofy.improvement.assignment

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}


trait AssignmentRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  get("/") {
    //FeatureServices.load(0, 50)
  }
}

class AssignmentAPI extends ScalatraServlet with FutureSupport with AssignmentRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
}
