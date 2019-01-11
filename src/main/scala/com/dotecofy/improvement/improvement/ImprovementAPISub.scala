
package com.dotecofy.improvement.improvement

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class ImprovementAPISub extends ScalatraServlet with ImprovementAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: ImprovementServicesComponentSub = ImprovementServicesSub
  override implicit val repository: ImprovementRepositoryComponentSub = ImprovementRepositorySub

  before() {
    contentType = formats("json")
  }
}