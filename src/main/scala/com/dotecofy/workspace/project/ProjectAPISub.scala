
package com.dotecofy.workspace.project

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class ProjectAPISub extends ScalatraServlet with ProjectAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: ProjectServicesComponentSub = ProjectServicesSub
  override implicit val repository: ProjectRepositoryComponentSub = ProjectRepositorySub

  before() {
    contentType = formats("json")
  }
}