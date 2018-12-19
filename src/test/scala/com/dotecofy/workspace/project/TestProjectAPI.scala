package com.dotecofy.workspace.project

import org.scalatra.test.specs2.MutableScalatraSpec

class TestProjectAPI extends MutableScalatraSpec {

  addServlet(classOf[ProjectAPI], "/projects")

  /*"GET /workspaces/1 on ProjectAPI" >> {
    "must return status 200" >> {
      get("/workspaces/1") {
        status must_== 200
      }
    }
  }*/

}
