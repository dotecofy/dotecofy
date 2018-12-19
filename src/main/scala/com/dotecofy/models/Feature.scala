package com.dotecofy.models

import java.time.ZonedDateTime

import scalikejdbc._

case class Feature(
                    id: Int,
                    idProject: Int,
                    signature: String,
                    name: String,
                    description: Option[String] = None,
                    createdDate: ZonedDateTime,
                    updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Feature.autoSession): Feature = Feature.save(this)(session)

  def destroy()(implicit session: DBSession = Feature.autoSession): Int = Feature.destroy(this)(session)

}


object Feature extends SQLSyntaxSupport[Feature] {

  override val schemaName = Some("dotecofy")

  override val tableName = "feature"

  override val columns = Seq("id", "id_project", "signature", "name", "description", "created_date", "updated_date")
  override val autoSession = AutoSession
  val f = Feature.syntax("f")

  def apply(f: SyntaxProvider[Feature])(rs: WrappedResultSet): Feature = apply(f.resultName)(rs)

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Feature] = {
    withSQL {
      select.from(Feature as f).where.eq(f.id, id)
    }.map(Feature(f.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Feature] = {
    withSQL(select.from(Feature as f)).map(Feature(f.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Feature as f)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Feature] = {
    withSQL {
      select.from(Feature as f).where.append(where)
    }.map(Feature(f.resultName)).single.apply()
  }

  def apply(f: ResultName[Feature])(rs: WrappedResultSet): Feature = new Feature(
    id = rs.get(f.id),
    idProject = rs.get(f.idProject),
    signature = rs.get(f.signature),
    name = rs.get(f.name),
    description = rs.get(f.description),
    createdDate = rs.get(f.createdDate),
    updatedDate = rs.get(f.updatedDate)
  )

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Feature] = {
    withSQL {
      select.from(Feature as f).where.append(where)
    }.map(Feature(f.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Feature as f).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
              idProject: Int,
              signature: String,
              name: String,
              description: Option[String] = None,
              createdDate: ZonedDateTime,
              updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Feature = {
    val generatedKey = withSQL {
      insert.into(Feature).namedValues(
        column.idProject -> idProject,
        column.signature -> signature,
        column.name -> name,
        column.description -> description,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Feature(
      id = generatedKey.toInt,
      idProject = idProject,
      signature = signature,
      name = name,
      description = description,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Feature])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idProject -> entity.idProject,
        'signature -> entity.signature,
        'name -> entity.name,
        'description -> entity.description,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL(
      """insert into feature(
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

  def save(entity: Feature)(implicit session: DBSession = autoSession): Feature = {
    withSQL {
      update(Feature).set(
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

  def destroy(entity: Feature)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete.from(Feature).where.eq(column.id, entity.id)
    }.update.apply()
  }

}
