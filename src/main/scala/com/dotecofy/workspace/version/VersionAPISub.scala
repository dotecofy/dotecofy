
package com.dotecofy.workspace.version

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class VersionAPISub extends ScalatraServlet with VersionAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: VersionServicesComponentSub = VersionServicesSub
  override implicit val repository: VersionRepositoryComponentSub = VersionRepositorySub

  before() {
    contentType = formats("json")
  }
}