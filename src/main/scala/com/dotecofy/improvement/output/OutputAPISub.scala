
package com.dotecofy.improvement.output

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class OutputAPISub extends ScalatraServlet with OutputAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: OutputServicesComponentSub = OutputServicesSub
  override implicit val repository: OutputRepositoryComponentSub = OutputRepositorySub

  before() {
    contentType = formats("json")
  }
}