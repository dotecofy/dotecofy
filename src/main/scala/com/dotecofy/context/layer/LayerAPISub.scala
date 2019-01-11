
package com.dotecofy.context.layer

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class LayerAPISub extends ScalatraServlet with LayerAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: LayerServicesComponentSub = LayerServicesSub
  override implicit val repository: LayerRepositoryComponentSub = LayerRepositorySub

  before() {
    contentType = formats("json")
  }
}