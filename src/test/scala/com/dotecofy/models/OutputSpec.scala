package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class OutputSpec extends Specification {

  "Output" should {

    val o = Output.syntax("o")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Output.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Output.findBy(sqls.eq(o.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Output.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Output.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Output.findAllBy(sqls.eq(o.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Output.countBy(sqls.eq(o.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Output.create(idAssignment = 123, idImprCycle = 123, createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Output.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Output.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Output.findAll().head
      val deleted = Output.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Output.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Output.findAll()
      entities.foreach(e => Output.destroy(e))
      val batchInserted = Output.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
