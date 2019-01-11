
package com.dotecofy.workspace.verification

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class VerificationAPISub extends ScalatraServlet with VerificationAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: VerificationServicesComponentSub = VerificationServicesSub
  override implicit val repository: VerificationRepositoryComponentSub = VerificationRepositorySub

  before() {
    contentType = formats("json")
  }
}