package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class TypeSpec extends Specification {

  "Type" should {

    val t = Type.syntax("t")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Type.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Type.findBy(sqls.eq(t.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Type.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Type.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Type.findAllBy(sqls.eq(t.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Type.countBy(sqls.eq(t.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Type.create(idProject = 123, signature = "MyString", name = "MyString", createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Type.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Type.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Type.findAll().head
      val deleted = Type.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Type.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Type.findAll()
      entities.foreach(e => Type.destroy(e))
      val batchInserted = Type.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
