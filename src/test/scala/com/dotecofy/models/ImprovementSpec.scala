package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class ImprovementSpec extends Specification {

  "Improvement" should {

    val i = Improvement.syntax("i")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Improvement.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Improvement.findBy(sqls.eq(i.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Improvement.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Improvement.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Improvement.findAllBy(sqls.eq(i.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Improvement.countBy(sqls.eq(i.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Improvement.create(id = 123, featureId = 123, versionId = 123, name = "MyString", signature = "MyString", createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Improvement.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Improvement.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Improvement.findAll().head
      val deleted = Improvement.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Improvement.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Improvement.findAll()
      entities.foreach(e => Improvement.destroy(e))
      val batchInserted = Improvement.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
