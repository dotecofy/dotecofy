package com.dotecofy.models

import scalikejdbc._
import java.time.{ZonedDateTime}

case class Output(
  id: Int,
  idAssignment: Int,
  idImprCycle: Int,
  remark: Option[String] = None,
  createdDate: ZonedDateTime,
  updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Output.autoSession): Output = Output.save(this)(session)

  def destroy()(implicit session: DBSession = Output.autoSession): Int = Output.destroy(this)(session)

}


object Output extends SQLSyntaxSupport[Output] {

  override val schemaName = Some("dotecofy")

  override val tableName = "output"

  override val columns = Seq("id", "id_assignment", "id_impr_cycle", "remark", "created_date", "updated_date")

  def apply(o: SyntaxProvider[Output])(rs: WrappedResultSet): Output = apply(o.resultName)(rs)
  def apply(o: ResultName[Output])(rs: WrappedResultSet): Output = new Output(
    id = rs.get(o.id),
    idAssignment = rs.get(o.idAssignment),
    idImprCycle = rs.get(o.idImprCycle),
    remark = rs.get(o.remark),
    createdDate = rs.get(o.createdDate),
    updatedDate = rs.get(o.updatedDate)
  )

  val o = Output.syntax("o")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Output] = {
    withSQL {
      select.from(Output as o).where.eq(o.id, id)
    }.map(Output(o.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Output] = {
    withSQL(select.from(Output as o)).map(Output(o.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Output as o)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Output] = {
    withSQL {
      select.from(Output as o).where.append(where)
    }.map(Output(o.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Output] = {
    withSQL {
      select.from(Output as o).where.append(where)
    }.map(Output(o.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Output as o).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    idAssignment: Int,
    idImprCycle: Int,
    remark: Option[String] = None,
    createdDate: ZonedDateTime,
    updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Output = {
    val generatedKey = withSQL {
      insert.into(Output).namedValues(
        column.idAssignment -> idAssignment,
        column.idImprCycle -> idImprCycle,
        column.remark -> remark,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Output(
      id = generatedKey.toInt,
      idAssignment = idAssignment,
      idImprCycle = idImprCycle,
      remark = remark,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Output])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idAssignment -> entity.idAssignment,
        'idImprCycle -> entity.idImprCycle,
        'remark -> entity.remark,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL("""insert into output(
      id_assignment,
      id_impr_cycle,
      remark,
      created_date,
      updated_date
    ) values (
      {idAssignment},
      {idImprCycle},
      {remark},
      {createdDate},
      {updatedDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Output)(implicit session: DBSession = autoSession): Output = {
    withSQL {
      update(Output).set(
        column.id -> entity.id,
        column.idAssignment -> entity.idAssignment,
        column.idImprCycle -> entity.idImprCycle,
        column.remark -> entity.remark,
        column.createdDate -> entity.createdDate,
        column.updatedDate -> entity.updatedDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Output)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Output).where.eq(column.id, entity.id) }.update.apply()
  }

}
