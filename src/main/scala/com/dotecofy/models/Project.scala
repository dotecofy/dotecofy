package com.dotecofy.models

import scalikejdbc._
import java.time.{ZonedDateTime}

case class Project(
  id: Int,
  idWorkspace: Int,
  signature: String,
  name: Option[String] = None,
  description: Option[String] = None,
  createdDate: ZonedDateTime,
  updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Project.autoSession): Project = Project.save(this)(session)

  def destroy()(implicit session: DBSession = Project.autoSession): Int = Project.destroy(this)(session)

}


object Project extends SQLSyntaxSupport[Project] {

  override val schemaName = Some("dotecofy")

  override val tableName = "project"

  override val columns = Seq("id", "id_workspace", "signature", "name", "description", "created_date", "updated_date")

  def apply(p: SyntaxProvider[Project])(rs: WrappedResultSet): Project = apply(p.resultName)(rs)
  def apply(p: ResultName[Project])(rs: WrappedResultSet): Project = new Project(
    id = rs.get(p.id),
    idWorkspace = rs.get(p.idWorkspace),
    signature = rs.get(p.signature),
    name = rs.get(p.name),
    description = rs.get(p.description),
    createdDate = rs.get(p.createdDate),
    updatedDate = rs.get(p.updatedDate)
  )

  val p = Project.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Project] = {
    withSQL {
      select.from(Project as p).where.eq(p.id, id)
    }.map(Project(p.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Project] = {
    withSQL(select.from(Project as p)).map(Project(p.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Project as p)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Project] = {
    withSQL {
      select.from(Project as p).where.append(where)
    }.map(Project(p.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Project] = {
    withSQL {
      select.from(Project as p).where.append(where)
    }.map(Project(p.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Project as p).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    idWorkspace: Int,
    signature: String,
    name: Option[String] = None,
    description: Option[String] = None,
    createdDate: ZonedDateTime,
    updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Project = {
    val generatedKey = withSQL {
      insert.into(Project).namedValues(
        column.idWorkspace -> idWorkspace,
        column.signature -> signature,
        column.name -> name,
        column.description -> description,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Project(
      id = generatedKey.toInt,
      idWorkspace = idWorkspace,
      signature = signature,
      name = name,
      description = description,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Project])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idWorkspace -> entity.idWorkspace,
        'signature -> entity.signature,
        'name -> entity.name,
        'description -> entity.description,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL("""insert into project(
      id_workspace,
      signature,
      name,
      description,
      created_date,
      updated_date
    ) values (
      {idWorkspace},
      {signature},
      {name},
      {description},
      {createdDate},
      {updatedDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Project)(implicit session: DBSession = autoSession): Project = {
    withSQL {
      update(Project).set(
        column.id -> entity.id,
        column.idWorkspace -> entity.idWorkspace,
        column.signature -> entity.signature,
        column.name -> entity.name,
        column.description -> entity.description,
        column.createdDate -> entity.createdDate,
        column.updatedDate -> entity.updatedDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Project)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Project).where.eq(column.id, entity.id) }.update.apply()
  }

}
