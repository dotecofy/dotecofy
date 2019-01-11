
package com.dotecofy.context.kind

import cloud.dest.sbf.api.ParseHeaders
import cloud.dest.sbf.common.FatherSearch.{ FSearchReq, FSearchRes }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase }
import org.scalatra.json.JacksonJsonSupport

import com.dotecofy.workspace.workspace._

trait KindAPI extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  implicit val services: KindServicesComponentSub = KindServicesSub
  implicit val repository: KindRepositoryComponentSub = KindRepositorySub
  implicit val repWorkspace: WorkspaceRepositoryComponentSub = WorkspaceRepositorySub

  get("/") {
    services.findByProfile(ParseHeaders.authorization(request).orNull, 0, 50)
  }

  get("/:signature") {
    services.findBySignature(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/workspace/:signature") {
    services.findByWorkspace(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/improvement/:signature") {
    services.findByImprovement(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/") {
    services.create(ParseHeaders.authorization(request).orNull, parsedBody.extract[KindSrv])
  }

  put("/:signature") {
    services.update(ParseHeaders.authorization(request).orNull, params("signature"), parsedBody.extract[KindSrv])
  }

  delete("/:signature") {
    services.delete(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/fsearch") {
    val req = parsedBody.extract[FSearchReq]
    val kinds = services.findByProfile(ParseHeaders.authorization(request).orNull, (req.pageNumber - 1) * req.pageSize, req.pageSize, (() => { if (req.searchKey == null || req.searchKey.isEmpty) { "name" } else { req.searchKey } }).apply(), req.searchValue).right.get
    FSearchRes(kinds.length, kinds)
  }

}
