
package com.dotecofy.improvement.improvementlayer

import cloud.dest.sbf.api.ParseHeaders
import cloud.dest.sbf.common.FatherSearch.{ FSearchReq, FSearchRes }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase }
import org.scalatra.json.JacksonJsonSupport

import com.dotecofy.improvement.improvement._
import com.dotecofy.context.layer._

trait ImprovementLayerAPI extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  implicit val services: ImprovementLayerServicesComponentSub = ImprovementLayerServicesSub
  implicit val repository: ImprovementLayerRepositoryComponentSub = ImprovementLayerRepositorySub
  implicit val repImprovement: ImprovementRepositoryComponentSub = ImprovementRepositorySub
  implicit val repLayer: LayerRepositoryComponentSub = LayerRepositorySub

  get("/") {
    services.findByProfile(ParseHeaders.authorization(request).orNull, 0, 50)
  }

  get("/:signature") {
    services.findBySignature(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/improvement/:signature") {
    services.findByImprovement(ParseHeaders.authorization(request).orNull, params("signature"))
  }
  get("/layer/:signature") {
    services.findByLayer(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/") {
    services.create(ParseHeaders.authorization(request).orNull, parsedBody.extract[ImprovementLayerSrv])
  }

  put("/:signature") {
    services.update(ParseHeaders.authorization(request).orNull, params("signature"), parsedBody.extract[ImprovementLayerSrv])
  }

  delete("/:signature") {
    services.delete(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/fsearch") {
    val req = parsedBody.extract[FSearchReq]
    val improvementlayers = services.findByProfile(ParseHeaders.authorization(request).orNull, (req.pageNumber - 1) * req.pageSize, req.pageSize, (() => { if (req.searchKey == null || req.searchKey.isEmpty) { "name" } else { req.searchKey } }).apply(), req.searchValue).right.get
    FSearchRes(improvementlayers.length, improvementlayers)
  }

}
