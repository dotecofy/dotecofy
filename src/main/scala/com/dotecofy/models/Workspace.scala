package com.dotecofy.models

import scalikejdbc._
import java.time.{ZonedDateTime}

case class Workspace(
  id: Int,
  signature: String,
  name: String,
  description: Option[String] = None,
  createdDate: ZonedDateTime,
  updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Workspace.autoSession): Workspace = Workspace.save(this)(session)

  def destroy()(implicit session: DBSession = Workspace.autoSession): Int = Workspace.destroy(this)(session)

}


object Workspace extends SQLSyntaxSupport[Workspace] {

  override val schemaName = Some("dotecofy")

  override val tableName = "workspace"

  override val columns = Seq("id", "signature", "name", "description", "created_date", "updated_date")

  def apply(w: SyntaxProvider[Workspace])(rs: WrappedResultSet): Workspace = apply(w.resultName)(rs)
  def apply(w: ResultName[Workspace])(rs: WrappedResultSet): Workspace = new Workspace(
    id = rs.get(w.id),
    signature = rs.get(w.signature),
    name = rs.get(w.name),
    description = rs.get(w.description),
    createdDate = rs.get(w.createdDate),
    updatedDate = rs.get(w.updatedDate)
  )

  val w = Workspace.syntax("w")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Workspace] = {
    withSQL {
      select.from(Workspace as w).where.eq(w.id, id)
    }.map(Workspace(w.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Workspace] = {
    withSQL(select.from(Workspace as w)).map(Workspace(w.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Workspace as w)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Workspace] = {
    withSQL {
      select.from(Workspace as w).where.append(where)
    }.map(Workspace(w.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Workspace] = {
    withSQL {
      select.from(Workspace as w).where.append(where)
    }.map(Workspace(w.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Workspace as w).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    signature: String,
    name: String,
    description: Option[String] = None,
    createdDate: ZonedDateTime,
    updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Workspace = {
    val generatedKey = withSQL {
      insert.into(Workspace).namedValues(
        column.signature -> signature,
        column.name -> name,
        column.description -> description,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Workspace(
      id = generatedKey.toInt,
      signature = signature,
      name = name,
      description = description,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Workspace])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'signature -> entity.signature,
        'name -> entity.name,
        'description -> entity.description,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL("""insert into workspace(
      signature,
      name,
      description,
      created_date,
      updated_date
    ) values (
      {signature},
      {name},
      {description},
      {createdDate},
      {updatedDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Workspace)(implicit session: DBSession = autoSession): Workspace = {
    withSQL {
      update(Workspace).set(
        column.id -> entity.id,
        column.signature -> entity.signature,
        column.name -> entity.name,
        column.description -> entity.description,
        column.createdDate -> entity.createdDate,
        column.updatedDate -> entity.updatedDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Workspace)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Workspace).where.eq(column.id, entity.id) }.update.apply()
  }

}
