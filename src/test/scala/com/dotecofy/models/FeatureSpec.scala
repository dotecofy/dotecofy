package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class FeatureSpec extends Specification {

  "Feature" should {

    val f = Feature.syntax("f")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Feature.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Feature.findBy(sqls.eq(f.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Feature.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Feature.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Feature.findAllBy(sqls.eq(f.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Feature.countBy(sqls.eq(f.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Feature.create(idProject = 123, signature = "MyString", name = "MyString", createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Feature.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Feature.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Feature.findAll().head
      val deleted = Feature.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Feature.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Feature.findAll()
      entities.foreach(e => Feature.destroy(e))
      val batchInserted = Feature.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
