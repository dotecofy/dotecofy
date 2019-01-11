
package com.dotecofy.workspace.feature

import org.json4s.{ DefaultFormats, Formats }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet }
import org.scalatra.json.JacksonJsonSupport

import scala.concurrent.ExecutionContext

class FeatureAPISub extends ScalatraServlet with FeatureAPI with FutureSupport {

  protected implicit def executor: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  override implicit val services: FeatureServicesComponentSub = FeatureServicesSub
  override implicit val repository: FeatureRepositoryComponentSub = FeatureRepositorySub

  before() {
    contentType = formats("json")
  }
}