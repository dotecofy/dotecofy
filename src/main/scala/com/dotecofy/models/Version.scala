package com.dotecofy.models

import java.time.ZonedDateTime

import scalikejdbc._

case class Version(
                    id: Int,
                    version: String,
                    description: Option[String] = None,
                    documentation: Option[String] = None,
                    createdDate: ZonedDateTime,
                    updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Version.autoSession): Version = Version.save(this)(session)

  def destroy()(implicit session: DBSession = Version.autoSession): Int = Version.destroy(this)(session)

}


object Version extends SQLSyntaxSupport[Version] {

  override val schemaName = Some("dotecofy")

  override val tableName = "version"

  override val columns = Seq("id", "version", "description", "documentation", "created_date", "updated_date")
  override val autoSession = AutoSession
  val v = Version.syntax("v")

  def apply(v: SyntaxProvider[Version])(rs: WrappedResultSet): Version = apply(v.resultName)(rs)

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Version] = {
    withSQL {
      select.from(Version as v).where.eq(v.id, id)
    }.map(Version(v.resultName)).single.apply()
  }

  def apply(v: ResultName[Version])(rs: WrappedResultSet): Version = new Version(
    id = rs.get(v.id),
    version = rs.get(v.version),
    description = rs.get(v.description),
    documentation = rs.get(v.documentation),
    createdDate = rs.get(v.createdDate),
    updatedDate = rs.get(v.updatedDate)
  )

  def findAll()(implicit session: DBSession = autoSession): List[Version] = {
    withSQL(select.from(Version as v)).map(Version(v.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Version as v)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Version] = {
    withSQL {
      select.from(Version as v).where.append(where)
    }.map(Version(v.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Version] = {
    withSQL {
      select.from(Version as v).where.append(where)
    }.map(Version(v.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Version as v).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
              version: String,
              description: Option[String] = None,
              documentation: Option[String] = None,
              createdDate: ZonedDateTime,
              updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Version = {
    val generatedKey = withSQL {
      insert.into(Version).namedValues(
        column.version -> version,
        column.description -> description,
        column.documentation -> documentation,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Version(
      id = generatedKey.toInt,
      version = version,
      description = description,
      documentation = documentation,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Version])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'version -> entity.version,
        'description -> entity.description,
        'documentation -> entity.documentation,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL(
      """insert into version(
      version,
      description,
      documentation,
      created_date,
      updated_date
    ) values (
      {version},
      {description},
      {documentation},
      {createdDate},
      {updatedDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Version)(implicit session: DBSession = autoSession): Version = {
    withSQL {
      update(Version).set(
        column.id -> entity.id,
        column.version -> entity.version,
        column.description -> entity.description,
        column.documentation -> entity.documentation,
        column.createdDate -> entity.createdDate,
        column.updatedDate -> entity.updatedDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Version)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete.from(Version).where.eq(column.id, entity.id)
    }.update.apply()
  }

}
