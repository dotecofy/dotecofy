
package com.dotecofy.workspace.verification

import cloud.dest.sbf.api.ParseHeaders
import cloud.dest.sbf.common.FatherSearch.{ FSearchReq, FSearchRes }
import org.scalatra.{ CorsSupport, FutureSupport, ScalatraBase }
import org.scalatra.json.JacksonJsonSupport

import com.dotecofy.improvement.output._

trait VerificationAPI extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  implicit val services: VerificationServicesComponentSub = VerificationServicesSub
  implicit val repository: VerificationRepositoryComponentSub = VerificationRepositorySub
  implicit val repOutput: OutputRepositoryComponentSub = OutputRepositorySub

  get("/") {
    services.findByProfile(ParseHeaders.authorization(request).orNull, 0, 50)
  }

  get("/:signature") {
    services.findBySignature(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  get("/output/:signature") {
    services.findByOutput(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/") {
    services.create(ParseHeaders.authorization(request).orNull, parsedBody.extract[VerificationSrv])
  }

  put("/:signature") {
    services.update(ParseHeaders.authorization(request).orNull, params("signature"), parsedBody.extract[VerificationSrv])
  }

  delete("/:signature") {
    services.delete(ParseHeaders.authorization(request).orNull, params("signature"))
  }

  post("/fsearch") {
    val req = parsedBody.extract[FSearchReq]
    val verifications = services.findByProfile(ParseHeaders.authorization(request).orNull, (req.pageNumber - 1) * req.pageSize, req.pageSize, (() => { if (req.searchKey == null || req.searchKey.isEmpty) { "name" } else { req.searchKey } }).apply(), req.searchValue).right.get
    FSearchRes(verifications.length, verifications)
  }

}
