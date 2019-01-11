
package com.dotecofy.workspace.version

import cloud.dest.sbf.api.ParseHeaders
import cloud.dest.sbf.common.FatherSearch.{ FSearchReq, FSearchRes }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase }
import org.scalatra.json.JacksonJsonSupport

import com.dotecofy.workspace.project._

trait VersionAPI extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  implicit val services: VersionServicesComponentSub = VersionServicesSub
  implicit val repository: VersionRepositoryComponentSub = VersionRepositorySub
  implicit val repProject: ProjectRepositoryComponentSub = ProjectRepositorySub

  get("/") {
    services.findByProfile(ParseHeaders.authorization(request).orNull, 0, 50)
  }

  get("/:signature") {
    services.findBySignature(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/project/:signature") {
    services.findByProject(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/improvement/:signature") {
    services.findByImprovement(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/") {
    services.create(ParseHeaders.authorization(request).orNull, parsedBody.extract[VersionSrv])
  }

  put("/:signature") {
    services.update(ParseHeaders.authorization(request).orNull, params("signature"), parsedBody.extract[VersionSrv])
  }

  delete("/:signature") {
    services.delete(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/fsearch") {
    val req = parsedBody.extract[FSearchReq]
    val versions = services.findByProfile(ParseHeaders.authorization(request).orNull, (req.pageNumber - 1) * req.pageSize, req.pageSize, (() => { if (req.searchKey == null || req.searchKey.isEmpty) { "name" } else { req.searchKey } }).apply(), req.searchValue).right.get
    FSearchRes(versions.length, versions)
  }

}
