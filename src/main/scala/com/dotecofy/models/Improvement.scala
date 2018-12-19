package com.dotecofy.models

import java.time.ZonedDateTime

import scalikejdbc._

case class Improvement(
                        id: Int,
                        featureId: Int,
                        versionId: Int,
                        name: String,
                        signature: String,
                        description: Option[String] = None,
                        documentation: Option[String] = None,
                        createdDate: ZonedDateTime,
                        updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Improvement.autoSession): Improvement = Improvement.save(this)(session)

  def destroy()(implicit session: DBSession = Improvement.autoSession): Int = Improvement.destroy(this)(session)

}


object Improvement extends SQLSyntaxSupport[Improvement] {

  override val schemaName = Some("dotecofy")

  override val tableName = "improvement"

  override val columns = Seq("id", "feature_id", "version_id", "name", "signature", "description", "documentation", "created_date", "updated_date")
  override val autoSession = AutoSession
  val i = Improvement.syntax("i")

  def apply(i: SyntaxProvider[Improvement])(rs: WrappedResultSet): Improvement = apply(i.resultName)(rs)

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Improvement] = {
    withSQL {
      select.from(Improvement as i).where.eq(i.id, id)
    }.map(Improvement(i.resultName)).single.apply()
  }

  def apply(i: ResultName[Improvement])(rs: WrappedResultSet): Improvement = new Improvement(
    id = rs.get(i.id),
    featureId = rs.get(i.featureId),
    versionId = rs.get(i.versionId),
    name = rs.get(i.name),
    signature = rs.get(i.signature),
    description = rs.get(i.description),
    documentation = rs.get(i.documentation),
    createdDate = rs.get(i.createdDate),
    updatedDate = rs.get(i.updatedDate)
  )

  def findAll()(implicit session: DBSession = autoSession): List[Improvement] = {
    withSQL(select.from(Improvement as i)).map(Improvement(i.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Improvement as i)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Improvement] = {
    withSQL {
      select.from(Improvement as i).where.append(where)
    }.map(Improvement(i.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Improvement] = {
    withSQL {
      select.from(Improvement as i).where.append(where)
    }.map(Improvement(i.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Improvement as i).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
              id: Int,
              featureId: Int,
              versionId: Int,
              name: String,
              signature: String,
              description: Option[String] = None,
              documentation: Option[String] = None,
              createdDate: ZonedDateTime,
              updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Improvement = {
    withSQL {
      insert.into(Improvement).namedValues(
        column.id -> id,
        column.featureId -> featureId,
        column.versionId -> versionId,
        column.name -> name,
        column.signature -> signature,
        column.description -> description,
        column.documentation -> documentation,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.update.apply()

    Improvement(
      id = id,
      featureId = featureId,
      versionId = versionId,
      name = name,
      signature = signature,
      description = description,
      documentation = documentation,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Improvement])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'id -> entity.id,
        'featureId -> entity.featureId,
        'versionId -> entity.versionId,
        'name -> entity.name,
        'signature -> entity.signature,
        'description -> entity.description,
        'documentation -> entity.documentation,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL(
      """insert into improvement(
      id,
      feature_id,
      version_id,
      name,
      signature,
      description,
      documentation,
      created_date,
      updated_date
    ) values (
      {id},
      {featureId},
      {versionId},
      {name},
      {signature},
      {description},
      {documentation},
      {createdDate},
      {updatedDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Improvement)(implicit session: DBSession = autoSession): Improvement = {
    withSQL {
      update(Improvement).set(
        column.id -> entity.id,
        column.featureId -> entity.featureId,
        column.versionId -> entity.versionId,
        column.name -> entity.name,
        column.signature -> entity.signature,
        column.description -> entity.description,
        column.documentation -> entity.documentation,
        column.createdDate -> entity.createdDate,
        column.updatedDate -> entity.updatedDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Improvement)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete.from(Improvement).where.eq(column.id, entity.id)
    }.update.apply()
  }

}
