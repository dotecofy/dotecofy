
package com.dotecofy.access.group

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class GroupAPISub extends ScalatraServlet with GroupAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: GroupServicesComponentSub = GroupServicesSub
  override implicit val repository: GroupRepositoryComponentSub = GroupRepositorySub

  before() {
    contentType = formats("json")
  }
}