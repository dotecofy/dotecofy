package com.dotecofy.workspace.workspace

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport

import com.dotecofy.models.User

trait WorkspaceRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport{

  val user = new User(id = 1, fullname = "Joël Favre", email = "joel.favre@dest.cloud", salt = "awdwadw", password="6aw4ef54", createdDate=null)

  implicit val repository:WorkspaceRepositoryComponent = WorkspaceRepository

  get("/") {
    WorkspaceServices.findByUser(user, 0, 50)
  }
}

class WorkspaceAPI extends ScalatraServlet with FutureSupport with WorkspaceRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
}