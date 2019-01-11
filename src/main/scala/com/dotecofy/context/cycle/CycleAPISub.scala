
package com.dotecofy.context.cycle

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class CycleAPISub extends ScalatraServlet with CycleAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: CycleServicesComponentSub = CycleServicesSub
  override implicit val repository: CycleRepositoryComponentSub = CycleRepositorySub

  before() {
    contentType = formats("json")
  }
}