package com.dotecofy.improvement.improvement

import com.dotecofy.improvement.assignment.AssignmentRoutes
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}

trait ImprovementRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  get("/") {
    //FeatureServices.load(0, 50)
  }
}

class ImprovementAPI extends ScalatraServlet with FutureSupport with ImprovementRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

}
