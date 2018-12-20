package com.dotecofy.workspace.feature

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}

trait FeatureRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  options("/*") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
  }

  get("/") {
    //FeatureServices.load(0, 50)
  }

  get("/page/:nb") {
    //featureComponent.featureServices.load(0,50)
  }

}

class FeatureAPI extends ScalatraServlet with FutureSupport with FeatureRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  //protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
}
