package com.dotecofy.models

import scalikejdbc._
import java.time.{ZonedDateTime}

case class Verification(
  id: Int,
  idOutput: Int,
  remark: Option[String] = None,
  verificationDate: ZonedDateTime,
  createdDate: ZonedDateTime,
  updateDate: Option[String] = None) {

  def save()(implicit session: DBSession = Verification.autoSession): Verification = Verification.save(this)(session)

  def destroy()(implicit session: DBSession = Verification.autoSession): Int = Verification.destroy(this)(session)

}


object Verification extends SQLSyntaxSupport[Verification] {

  override val schemaName = Some("dotecofy")

  override val tableName = "verification"

  override val columns = Seq("id", "id_output", "remark", "verification_date", "created_date", "update_date")

  def apply(v: SyntaxProvider[Verification])(rs: WrappedResultSet): Verification = apply(v.resultName)(rs)
  def apply(v: ResultName[Verification])(rs: WrappedResultSet): Verification = new Verification(
    id = rs.get(v.id),
    idOutput = rs.get(v.idOutput),
    remark = rs.get(v.remark),
    verificationDate = rs.get(v.verificationDate),
    createdDate = rs.get(v.createdDate),
    updateDate = rs.get(v.updateDate)
  )

  val v = Verification.syntax("v")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Verification] = {
    withSQL {
      select.from(Verification as v).where.eq(v.id, id)
    }.map(Verification(v.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Verification] = {
    withSQL(select.from(Verification as v)).map(Verification(v.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Verification as v)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Verification] = {
    withSQL {
      select.from(Verification as v).where.append(where)
    }.map(Verification(v.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Verification] = {
    withSQL {
      select.from(Verification as v).where.append(where)
    }.map(Verification(v.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Verification as v).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    idOutput: Int,
    remark: Option[String] = None,
    verificationDate: ZonedDateTime,
    createdDate: ZonedDateTime,
    updateDate: Option[String] = None)(implicit session: DBSession = autoSession): Verification = {
    val generatedKey = withSQL {
      insert.into(Verification).namedValues(
        column.idOutput -> idOutput,
        column.remark -> remark,
        column.verificationDate -> verificationDate,
        column.createdDate -> createdDate,
        column.updateDate -> updateDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Verification(
      id = generatedKey.toInt,
      idOutput = idOutput,
      remark = remark,
      verificationDate = verificationDate,
      createdDate = createdDate,
      updateDate = updateDate)
  }

  def batchInsert(entities: collection.Seq[Verification])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idOutput -> entity.idOutput,
        'remark -> entity.remark,
        'verificationDate -> entity.verificationDate,
        'createdDate -> entity.createdDate,
        'updateDate -> entity.updateDate))
    SQL("""insert into verification(
      id_output,
      remark,
      verification_date,
      created_date,
      update_date
    ) values (
      {idOutput},
      {remark},
      {verificationDate},
      {createdDate},
      {updateDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Verification)(implicit session: DBSession = autoSession): Verification = {
    withSQL {
      update(Verification).set(
        column.id -> entity.id,
        column.idOutput -> entity.idOutput,
        column.remark -> entity.remark,
        column.verificationDate -> entity.verificationDate,
        column.createdDate -> entity.createdDate,
        column.updateDate -> entity.updateDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Verification)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Verification).where.eq(column.id, entity.id) }.update.apply()
  }

}
