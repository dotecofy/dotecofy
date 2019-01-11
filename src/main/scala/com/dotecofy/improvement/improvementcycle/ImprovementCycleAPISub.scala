
package com.dotecofy.improvement.improvementcycle

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class ImprovementCycleAPISub extends ScalatraServlet with ImprovementCycleAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: ImprovementCycleServicesComponentSub = ImprovementCycleServicesSub
  override implicit val repository: ImprovementCycleRepositoryComponentSub = ImprovementCycleRepositorySub

  before() {
    contentType = formats("json")
  }
}