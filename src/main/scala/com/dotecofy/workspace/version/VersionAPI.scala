package com.dotecofy.workspace.version

import com.dotecofy.workspace.feature.VersionServices
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport

trait VersionRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport{

  get("/") {
    //VersionServices.load(0, 50)
  }
}

class VersionAPI extends ScalatraServlet with FutureSupport with VersionRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
}