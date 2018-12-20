package com.dotecofy.workspace.project

import com.dotecofy.models.{Project, User}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{CorsSupport, FutureSupport, ScalatraBase, ScalatraServlet}

trait ProjectRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport with CorsSupport {

  val user = new User(id = 1, fullname = "Joël Favre", email = "joel.favre@dest.cloud", salt = "awdwadw", password = "6aw4ef54", createdDate = null)

  implicit val repository: ProjectRepositoryComponent = ProjectRepository

  get("/") {
    val workspace = params.get("workspace")
    ProjectServices.findByWorkspace(user, workspace.get, 0, 50)
  }

  post("/workspaces/:signature") {
    ProjectServices.create(user, params("signature"), parsedBody.extract[Project])
  }

  put("/:signature") {

  }

  delete("/:signature") {

  }

}

class ProjectAPI extends ScalatraServlet with FutureSupport with ProjectRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  //protected implicit lazy val jsonFormats: Formats = defaultAcceptedFormats.withBigDecimal
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }
}
