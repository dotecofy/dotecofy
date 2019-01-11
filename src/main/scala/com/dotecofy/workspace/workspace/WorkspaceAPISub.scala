
package com.dotecofy.workspace.workspace

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class WorkspaceAPISub extends ScalatraServlet with WorkspaceAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: WorkspaceServicesComponentSub = WorkspaceServicesSub
  override implicit val repository: WorkspaceRepositoryComponentSub = WorkspaceRepositorySub

  before() {
    contentType = formats("json")
  }
}