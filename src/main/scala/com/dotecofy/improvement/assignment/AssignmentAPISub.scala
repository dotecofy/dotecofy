
package com.dotecofy.improvement.assignment

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class AssignmentAPISub extends ScalatraServlet with AssignmentAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: AssignmentServicesComponentSub = AssignmentServicesSub
  override implicit val repository: AssignmentRepositoryComponentSub = AssignmentRepositorySub

  before() {
    contentType = formats("json")
  }
}