package com.dotecofy.workspace.workspace

import java.time.ZonedDateTime

import com.dotecofy.models.{User, Workspace}
import org.specs2.mutable.Specification

class TestWorkspaceServices extends Specification {

    val user = new User(id = 1, fullname = "Peter Parker", email = "peter.parker@example.com", salt = null, password = null, createdDate = null)

    "Workspace fields" >> {
        "signature should not be over 80" >> {
            WorkspaceServices.create(user,  new Workspace(1,  "012345678901234567890123456789012345678901234567890123456789012345678901234567890", "project 1", Option("Description prj 1"), ZonedDateTime.now(), null)) must beLeft
        }

        "signature should not be less than 3" >> {
            WorkspaceServices.create(user, new Project(1,  "01", "project 1", Option("Description prj 1"), ZonedDateTime.now(), null)) must beLeft
        }

        "name should not be over 50" >> {
            WorkspaceServices.create(user, new Project(1,  "signature", "012345678901234567890123456789012345678901234567890", Option("Description prj 1"), ZonedDateTime.now(), null)) must beLeft
        }

        "name should not be less than 3" >> {
            WorkspaceServices.create(user, new Project(1,  "signature", "01", Option("Description prj 1"), ZonedDateTime.now(), null)) must beLeft
        }

        "signature should not be over 200" >> {
            val value = (scala.util.Random.alphanumeric take 201).mkString
            WorkspaceServices.create(user, new Project(1,  "signature", "project 1", Option(value), ZonedDateTime.now(), null)) must beLeft
        }
    }
}