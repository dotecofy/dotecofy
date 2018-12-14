package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class LayerSpec extends Specification {

  "Layer" should {

    val l = Layer.syntax("l")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Layer.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Layer.findBy(sqls.eq(l.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Layer.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Layer.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Layer.findAllBy(sqls.eq(l.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Layer.countBy(sqls.eq(l.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Layer.create(idProject = 123, signature = "MyString", name = "MyString", createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Layer.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Layer.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Layer.findAll().head
      val deleted = Layer.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Layer.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Layer.findAll()
      entities.foreach(e => Layer.destroy(e))
      val batchInserted = Layer.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
