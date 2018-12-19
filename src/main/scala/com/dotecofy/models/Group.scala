package com.dotecofy.models

import java.time.ZonedDateTime

import scalikejdbc._

case class Group(
                  id: Int,
                  idProject: Int,
                  signature: Option[String] = None,
                  name: String,
                  description: Option[String] = None,
                  createdDate: ZonedDateTime,
                  updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Group.autoSession): Group = Group.save(this)(session)

  def destroy()(implicit session: DBSession = Group.autoSession): Int = Group.destroy(this)(session)

}


object Group extends SQLSyntaxSupport[Group] {

  override val schemaName = Some("dotecofy")

  override val tableName = "group"

  override val columns = Seq("id", "id_project", "signature", "name", "description", "created_date", "updated_date")
  override val autoSession = AutoSession
  val g = Group.syntax("g")

  def apply(g: SyntaxProvider[Group])(rs: WrappedResultSet): Group = apply(g.resultName)(rs)

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Group] = {
    withSQL {
      select.from(Group as g).where.eq(g.id, id)
    }.map(Group(g.resultName)).single.apply()
  }

  def apply(g: ResultName[Group])(rs: WrappedResultSet): Group = new Group(
    id = rs.get(g.id),
    idProject = rs.get(g.idProject),
    signature = rs.get(g.signature),
    name = rs.get(g.name),
    description = rs.get(g.description),
    createdDate = rs.get(g.createdDate),
    updatedDate = rs.get(g.updatedDate)
  )

  def findAll()(implicit session: DBSession = autoSession): List[Group] = {
    withSQL(select.from(Group as g)).map(Group(g.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Group as g)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Group] = {
    withSQL {
      select.from(Group as g).where.append(where)
    }.map(Group(g.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Group] = {
    withSQL {
      select.from(Group as g).where.append(where)
    }.map(Group(g.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Group as g).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
              idProject: Int,
              signature: Option[String] = None,
              name: String,
              description: Option[String] = None,
              createdDate: ZonedDateTime,
              updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Group = {
    val generatedKey = withSQL {
      insert.into(Group).namedValues(
        column.idProject -> idProject,
        column.signature -> signature,
        column.name -> name,
        column.description -> description,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Group(
      id = generatedKey.toInt,
      idProject = idProject,
      signature = signature,
      name = name,
      description = description,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Group])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idProject -> entity.idProject,
        'signature -> entity.signature,
        'name -> entity.name,
        'description -> entity.description,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL(
      """insert into group(
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

  def save(entity: Group)(implicit session: DBSession = autoSession): Group = {
    withSQL {
      update(Group).set(
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

  def destroy(entity: Group)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete.from(Group).where.eq(column.id, entity.id)
    }.update.apply()
  }

}
