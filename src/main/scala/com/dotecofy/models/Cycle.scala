package com.dotecofy.models

import java.time.ZonedDateTime

import scalikejdbc._

case class Cycle(
                  id: Int,
                  idProject: Int,
                  signature: String,
                  name: String,
                  description: Option[String] = None,
                  createdDate: ZonedDateTime,
                  updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Cycle.autoSession): Cycle = Cycle.save(this)(session)

  def destroy()(implicit session: DBSession = Cycle.autoSession): Int = Cycle.destroy(this)(session)

}


object Cycle extends SQLSyntaxSupport[Cycle] {

  override val schemaName = Some("dotecofy")

  override val tableName = "cycle"

  override val columns = Seq("id", "id_project", "signature", "name", "description", "created_date", "updated_date")
  override val autoSession = AutoSession
  val c = Cycle.syntax("c")

  def apply(c: SyntaxProvider[Cycle])(rs: WrappedResultSet): Cycle = apply(c.resultName)(rs)

  def apply(c: ResultName[Cycle])(rs: WrappedResultSet): Cycle = new Cycle(
    id = rs.get(c.id),
    idProject = rs.get(c.idProject),
    signature = rs.get(c.signature),
    name = rs.get(c.name),
    description = rs.get(c.description),
    createdDate = rs.get(c.createdDate),
    updatedDate = rs.get(c.updatedDate)
  )

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Cycle] = {
    withSQL {
      select.from(Cycle as c).where.eq(c.id, id)
    }.map(Cycle(c.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Cycle] = {
    withSQL(select.from(Cycle as c)).map(Cycle(c.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Cycle as c)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Cycle] = {
    withSQL {
      select.from(Cycle as c).where.append(where)
    }.map(Cycle(c.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Cycle] = {
    withSQL {
      select.from(Cycle as c).where.append(where)
    }.map(Cycle(c.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Cycle as c).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
              idProject: Int,
              signature: String,
              name: String,
              description: Option[String] = None,
              createdDate: ZonedDateTime,
              updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Cycle = {
    val generatedKey = withSQL {
      insert.into(Cycle).namedValues(
        column.idProject -> idProject,
        column.signature -> signature,
        column.name -> name,
        column.description -> description,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Cycle(
      id = generatedKey.toInt,
      idProject = idProject,
      signature = signature,
      name = name,
      description = description,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Cycle])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idProject -> entity.idProject,
        'signature -> entity.signature,
        'name -> entity.name,
        'description -> entity.description,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL(
      """insert into cycle(
      id_project,
      signature,
      name,
      description,
      created_date,
      updated_date
    ) values (
      {idProject},
      {signature},
      {name},
      {description},
      {createdDate},
      {updatedDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Cycle)(implicit session: DBSession = autoSession): Cycle = {
    withSQL {
      update(Cycle).set(
        column.id -> entity.id,
        column.idProject -> entity.idProject,
        column.signature -> entity.signature,
        column.name -> entity.name,
        column.description -> entity.description,
        column.createdDate -> entity.createdDate,
        column.updatedDate -> entity.updatedDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Cycle)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete.from(Cycle).where.eq(column.id, entity.id)
    }.update.apply()
  }

}
