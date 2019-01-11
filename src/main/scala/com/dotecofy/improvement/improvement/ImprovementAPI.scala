
package com.dotecofy.improvement.improvement

import cloud.dest.sbf.api.ParseHeaders
import cloud.dest.sbf.common.FatherSearch.{ FSearchReq, FSearchRes }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase }
import org.scalatra.json.JacksonJsonSupport

import com.dotecofy.workspace.feature._

trait ImprovementAPI extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  implicit val services: ImprovementServicesComponentSub = ImprovementServicesSub
  implicit val repository: ImprovementRepositoryComponentSub = ImprovementRepositorySub
  implicit val repFeature: FeatureRepositoryComponentSub = FeatureRepositorySub

  get("/") {
    services.findByProfile(ParseHeaders.authorization(request).orNull, 0, 50)
  }

  get("/:signature") {
    services.findBySignature(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/feature/:signature") {
    services.findByFeature(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/cycle/:signature") {
    services.findByCycle(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/layer/:signature") {
    services.findByLayer(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/version/:signature") {
    services.findByVersion(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/kind/:signature") {
    services.findByKind(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/") {
    services.create(ParseHeaders.authorization(request).orNull, parsedBody.extract[ImprovementSrv])
  }

  put("/:signature") {
    services.update(ParseHeaders.authorization(request).orNull, params("signature"), parsedBody.extract[ImprovementSrv])
  }

  delete("/:signature") {
    services.delete(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/fsearch") {
    val req = parsedBody.extract[FSearchReq]
    val improvements = services.findByProfile(ParseHeaders.authorization(request).orNull, (req.pageNumber - 1) * req.pageSize, req.pageSize, (() => { if (req.searchKey == null || req.searchKey.isEmpty) { "name" } else { req.searchKey } }).apply(), req.searchValue).right.get
    FSearchRes(improvements.length, improvements)
  }

}
