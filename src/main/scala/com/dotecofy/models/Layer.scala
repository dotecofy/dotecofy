package com.dotecofy.models

import scalikejdbc._
import java.time.{ZonedDateTime}

case class Layer(
  id: Int,
  idProject: Int,
  signature: String,
  name: String,
  description: Option[String] = None,
  createdDate: ZonedDateTime,
  updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Layer.autoSession): Layer = Layer.save(this)(session)

  def destroy()(implicit session: DBSession = Layer.autoSession): Int = Layer.destroy(this)(session)

}


object Layer extends SQLSyntaxSupport[Layer] {

  override val schemaName = Some("dotecofy")

  override val tableName = "layer"

  override val columns = Seq("id", "id_project", "signature", "name", "description", "created_date", "updated_date")

  def apply(l: SyntaxProvider[Layer])(rs: WrappedResultSet): Layer = apply(l.resultName)(rs)
  def apply(l: ResultName[Layer])(rs: WrappedResultSet): Layer = new Layer(
    id = rs.get(l.id),
    idProject = rs.get(l.idProject),
    signature = rs.get(l.signature),
    name = rs.get(l.name),
    description = rs.get(l.description),
    createdDate = rs.get(l.createdDate),
    updatedDate = rs.get(l.updatedDate)
  )

  val l = Layer.syntax("l")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Layer] = {
    withSQL {
      select.from(Layer as l).where.eq(l.id, id)
    }.map(Layer(l.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Layer] = {
    withSQL(select.from(Layer as l)).map(Layer(l.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Layer as l)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Layer] = {
    withSQL {
      select.from(Layer as l).where.append(where)
    }.map(Layer(l.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Layer] = {
    withSQL {
      select.from(Layer as l).where.append(where)
    }.map(Layer(l.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Layer as l).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    idProject: Int,
    signature: String,
    name: String,
    description: Option[String] = None,
    createdDate: ZonedDateTime,
    updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Layer = {
    val generatedKey = withSQL {
      insert.into(Layer).namedValues(
        column.idProject -> idProject,
        column.signature -> signature,
        column.name -> name,
        column.description -> description,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Layer(
      id = generatedKey.toInt,
      idProject = idProject,
      signature = signature,
      name = name,
      description = description,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Layer])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idProject -> entity.idProject,
        'signature -> entity.signature,
        'name -> entity.name,
        'description -> entity.description,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL("""insert into layer(
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

  def save(entity: Layer)(implicit session: DBSession = autoSession): Layer = {
    withSQL {
      update(Layer).set(
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

  def destroy(entity: Layer)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Layer).where.eq(column.id, entity.id) }.update.apply()
  }

}
