package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class AssignmentSpec extends Specification {

  "Assignment" should {

    val a = Assignment.syntax("a")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Assignment.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Assignment.findBy(sqls.eq(a.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Assignment.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Assignment.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Assignment.findAllBy(sqls.eq(a.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Assignment.countBy(sqls.eq(a.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Assignment.create(idImprType = 123, idImprovement = 123, signature = "MyString", name = "MyString", createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Assignment.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Assignment.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Assignment.findAll().head
      val deleted = Assignment.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Assignment.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Assignment.findAll()
      entities.foreach(e => Assignment.destroy(e))
      val batchInserted = Assignment.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
