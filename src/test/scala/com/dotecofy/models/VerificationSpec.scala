package com.dotecofy.models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{ZonedDateTime}


class VerificationSpec extends Specification {

  "Verification" should {

    val v = Verification.syntax("v")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Verification.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Verification.findBy(sqls.eq(v.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Verification.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Verification.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Verification.findAllBy(sqls.eq(v.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Verification.countBy(sqls.eq(v.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Verification.create(idOutput = 123, verificationDate = null, createdDate = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Verification.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Verification.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Verification.findAll().head
      val deleted = Verification.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Verification.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Verification.findAll()
      entities.foreach(e => Verification.destroy(e))
      val batchInserted = Verification.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
