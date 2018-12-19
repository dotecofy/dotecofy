package com.dotecofy.context.layer

import com.dotecofy.improvement.assignment.AssignmentRoutes
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport

trait LayerRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  get("/") {
    //FeatureServices.load(0, 50)
  }
}

class LayerAPI extends ScalatraServlet with FutureSupport with LayerRoutes{

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
}
