package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class VersionSpec extends Specification {

  "Version" should {

    val v = Version.syntax("v")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Version.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Version.findBy(sqls.eq(v.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Version.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Version.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Version.findAllBy(sqls.eq(v.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Version.countBy(sqls.eq(v.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Version.create(version = "MyString", createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Version.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Version.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Version.findAll().head
      val deleted = Version.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Version.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Version.findAll()
      entities.foreach(e => Version.destroy(e))
      val batchInserted = Version.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
