package com.dotecofy.models

import java.time.ZonedDateTime

import scalikejdbc._

case class Type(
                 id: Int,
                 idProject: Int,
                 signature: String,
                 name: String,
                 description: Option[String] = None,
                 createdDate: ZonedDateTime,
                 updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Type.autoSession): Type = Type.save(this)(session)

  def destroy()(implicit session: DBSession = Type.autoSession): Int = Type.destroy(this)(session)

}


object Type extends SQLSyntaxSupport[Type] {

  override val schemaName = Some("dotecofy")

  override val tableName = "type"

  override val columns = Seq("id", "id_project", "signature", "name", "description", "created_date", "updated_date")
  override val autoSession = AutoSession
  val t = Type.syntax("t")

  def apply(t: SyntaxProvider[Type])(rs: WrappedResultSet): Type = apply(t.resultName)(rs)

  def apply(t: ResultName[Type])(rs: WrappedResultSet): Type = new Type(
    id = rs.get(t.id),
    idProject = rs.get(t.idProject),
    signature = rs.get(t.signature),
    name = rs.get(t.name),
    description = rs.get(t.description),
    createdDate = rs.get(t.createdDate),
    updatedDate = rs.get(t.updatedDate)
  )

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Type] = {
    withSQL {
      select.from(Type as t).where.eq(t.id, id)
    }.map(Type(t.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Type] = {
    withSQL(select.from(Type as t)).map(Type(t.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Type as t)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Type] = {
    withSQL {
      select.from(Type as t).where.append(where)
    }.map(Type(t.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Type] = {
    withSQL {
      select.from(Type as t).where.append(where)
    }.map(Type(t.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Type as t).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
              idProject: Int,
              signature: String,
              name: String,
              description: Option[String] = None,
              createdDate: ZonedDateTime,
              updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Type = {
    val generatedKey = withSQL {
      insert.into(Type).namedValues(
        column.idProject -> idProject,
        column.signature -> signature,
        column.name -> name,
        column.description -> description,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Type(
      id = generatedKey.toInt,
      idProject = idProject,
      signature = signature,
      name = name,
      description = description,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Type])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idProject -> entity.idProject,
        'signature -> entity.signature,
        'name -> entity.name,
        'description -> entity.description,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL(
      """insert into type(
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

  def save(entity: Type)(implicit session: DBSession = autoSession): Type = {
    withSQL {
      update(Type).set(
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

  def destroy(entity: Type)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete.from(Type).where.eq(column.id, entity.id)
    }.update.apply()
  }

}
