
package com.dotecofy.access.user

import cloud.dest.sbf.api.ParseHeaders
import cloud.dest.sbf.common.FatherSearch.{ FSearchReq, FSearchRes }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase }
import org.scalatra.json.JacksonJsonSupport

trait UserAPI extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  implicit val services: UserServicesComponentSub = UserServicesSub
  implicit val repository: UserRepositoryComponentSub = UserRepositorySub

  get("/") {
    services.findByProfile(ParseHeaders.authorization(request).orNull, 0, 50)
  }

  get("/:signature") {
    services.findBySignature(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/") {
    services.create(ParseHeaders.authorization(request).orNull, parsedBody.extract[UserSrv])
  }

  put("/:signature") {
    services.update(ParseHeaders.authorization(request).orNull, params("signature"), parsedBody.extract[UserSrv])
  }

  delete("/:signature") {
    services.delete(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/fsearch") {
    val req = parsedBody.extract[FSearchReq]
    val users = services.findByProfile(ParseHeaders.authorization(request).orNull, (req.pageNumber - 1) * req.pageSize, req.pageSize, (() => { if (req.searchKey == null || req.searchKey.isEmpty) { "name" } else { req.searchKey } }).apply(), req.searchValue).right.get
    FSearchRes(users.length, users)
  }

}
