
package com.dotecofy.access.user

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class UserAPISub extends ScalatraServlet with UserAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: UserServicesComponentSub = UserServicesSub
  override implicit val repository: UserRepositoryComponentSub = UserRepositorySub

  before() {
    contentType = formats("json")
  }
}