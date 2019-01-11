
package com.dotecofy.improvement.improvementlayer

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class ImprovementLayerAPISub extends ScalatraServlet with ImprovementLayerAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: ImprovementLayerServicesComponentSub = ImprovementLayerServicesSub
  override implicit val repository: ImprovementLayerRepositoryComponentSub = ImprovementLayerRepositorySub

  before() {
    contentType = formats("json")
  }
}