package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class WorkspaceSpec extends Specification {

  "Workspace" should {

    val w = Workspace.syntax("w")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Workspace.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Workspace.findBy(sqls.eq(w.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Workspace.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Workspace.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Workspace.findAllBy(sqls.eq(w.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Workspace.countBy(sqls.eq(w.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Workspace.create(signature = "MyString", name = "MyString", createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Workspace.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Workspace.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Workspace.findAll().head
      val deleted = Workspace.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Workspace.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Workspace.findAll()
      entities.foreach(e => Workspace.destroy(e))
      val batchInserted = Workspace.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
