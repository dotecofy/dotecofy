
package com.dotecofy.improvement.improvementcycle

import cloud.dest.sbf.api.ParseHeaders
import cloud.dest.sbf.common.FatherSearch.{ FSearchReq, FSearchRes }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase }
import org.scalatra.json.JacksonJsonSupport

import com.dotecofy.improvement.improvement._
import com.dotecofy.context.cycle._

trait ImprovementCycleAPI extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  implicit val services: ImprovementCycleServicesComponentSub = ImprovementCycleServicesSub
  implicit val repository: ImprovementCycleRepositoryComponentSub = ImprovementCycleRepositorySub
  implicit val repImprovement: ImprovementRepositoryComponentSub = ImprovementRepositorySub
  implicit val repCycle: CycleRepositoryComponentSub = CycleRepositorySub

  get("/") {
    services.findByProfile(ParseHeaders.authorization(request).orNull, 0, 50)
  }

  get("/:signature") {
    services.findBySignature(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/improvement/:signature") {
    services.findByImprovement(ParseHeaders.authorization(request).orNull, params("signature"))
  }
  get("/cycle/:signature") {
    services.findByCycle(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/") {
    services.create(ParseHeaders.authorization(request).orNull, parsedBody.extract[ImprovementCycleSrv])
  }

  put("/:signature") {
    services.update(ParseHeaders.authorization(request).orNull, params("signature"), parsedBody.extract[ImprovementCycleSrv])
  }

  delete("/:signature") {
    services.delete(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/fsearch") {
    val req = parsedBody.extract[FSearchReq]
    val improvementcycles = services.findByProfile(ParseHeaders.authorization(request).orNull, (req.pageNumber - 1) * req.pageSize, req.pageSize, (() => { if (req.searchKey == null || req.searchKey.isEmpty) { "name" } else { req.searchKey } }).apply(), req.searchValue).right.get
    FSearchRes(improvementcycles.length, improvementcycles)
  }

}
