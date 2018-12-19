package com.dotecofy.workspace.project

import java.time.ZonedDateTime

import com.dotecofy.models.{Project, User}
import org.specs2.mutable.Specification

import scala.language.postfixOps

class TestProjectServices extends Specification {

  val user = new User(id = 1, fullname = "Peter Parker", email = "peter.parker@example.com", salt = null, password = null, createdDate = null)

  "Project fields" >> {
    "signature should not be over 80" >> {
      ProjectServices.create(user, "", new Project(1, 1, "012345678901234567890123456789012345678901234567890123456789012345678901234567890", "project 1", Option("Description prj 1"), ZonedDateTime.now(), null)) must beLeft
    }

    "signature should not be less than 3" >> {
      ProjectServices.create(user, "", new Project(1, 1, "01", "project 1", Option("Description prj 1"), ZonedDateTime.now(), null)) must beLeft
    }

    "name should not be over 50" >> {
      ProjectServices.create(user, "", new Project(1, 1, "signature", "012345678901234567890123456789012345678901234567890", Option("Description prj 1"), ZonedDateTime.now(), null)) must beLeft
    }

    "name should not be less than 3" >> {
      ProjectServices.create(user, "", new Project(1, 1, "signature", "01", Option("Description prj 1"), ZonedDateTime.now(), null)) must beLeft
    }

    "signature should not be over 200" >> {
      val value = (scala.util.Random.alphanumeric take 201).mkString
      ProjectServices.create(user, "", new Project(1, 1, "signature", "project 1", Option(value), ZonedDateTime.now(), null)) must beLeft
    }

  }

}
