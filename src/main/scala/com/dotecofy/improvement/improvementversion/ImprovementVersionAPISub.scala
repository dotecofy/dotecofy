
package com.dotecofy.improvement.improvementversion

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class ImprovementVersionAPISub extends ScalatraServlet with ImprovementVersionAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: ImprovementVersionServicesComponentSub = ImprovementVersionServicesSub
  override implicit val repository: ImprovementVersionRepositoryComponentSub = ImprovementVersionRepositorySub

  before() {
    contentType = formats("json")
  }
}