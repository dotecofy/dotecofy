package com.dotecofy.context.cycle

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}

trait CycleRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  get("/") {
    //FeatureServices.load(0, 50)
  }
}

class CycleAPI extends ScalatraServlet with FutureSupport with CycleRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
}
