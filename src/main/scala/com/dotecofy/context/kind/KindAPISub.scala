
package com.dotecofy.context.kind

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class KindAPISub extends ScalatraServlet with KindAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: KindServicesComponentSub = KindServicesSub
  override implicit val repository: KindRepositoryComponentSub = KindRepositorySub

  before() {
    contentType = formats("json")
  }
}