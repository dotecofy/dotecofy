package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class CycleSpec extends Specification {

  "Cycle" should {

    val c = Cycle.syntax("c")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Cycle.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Cycle.findBy(sqls.eq(c.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Cycle.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Cycle.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Cycle.findAllBy(sqls.eq(c.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Cycle.countBy(sqls.eq(c.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Cycle.create(idProject = 123, signature = "MyString", name = "MyString", createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Cycle.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Cycle.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Cycle.findAll().head
      val deleted = Cycle.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Cycle.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Cycle.findAll()
      entities.foreach(e => Cycle.destroy(e))
      val batchInserted = Cycle.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
