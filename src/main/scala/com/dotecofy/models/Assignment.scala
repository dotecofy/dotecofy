package com.dotecofy.models

import java.time.ZonedDateTime

import scalikejdbc._

case class Assignment(
                       id: Int,
                       idImprType: Int,
                       idImprovement: Int,
                       signature: String,
                       name: String,
                       description: Option[String] = None,
                       createdDate: ZonedDateTime,
                       updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Assignment.autoSession): Assignment = Assignment.save(this)(session)

  def destroy()(implicit session: DBSession = Assignment.autoSession): Int = Assignment.destroy(this)(session)

}


object Assignment extends SQLSyntaxSupport[Assignment] {

  override val schemaName = Some("dotecofy")

  override val tableName = "assignment"

  override val columns = Seq("id", "id_impr_type", "id_improvement", "signature", "name", "description", "created_date", "updated_date")
  override val autoSession = AutoSession
  val a = Assignment.syntax("a")

  def apply(a: SyntaxProvider[Assignment])(rs: WrappedResultSet): Assignment = apply(a.resultName)(rs)

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Assignment] = {
    withSQL {
      select.from(Assignment as a).where.eq(a.id, id)
    }.map(Assignment(a.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Assignment] = {
    withSQL(select.from(Assignment as a)).map(Assignment(a.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Assignment as a)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Assignment] = {
    withSQL {
      select.from(Assignment as a).where.append(where)
    }.map(Assignment(a.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Assignment] = {
    withSQL {
      select.from(Assignment as a).where.append(where)
    }.map(Assignment(a.resultName)).list.apply()
  }

  def apply(a: ResultName[Assignment])(rs: WrappedResultSet): Assignment = new Assignment(
    id = rs.get(a.id),
    idImprType = rs.get(a.idImprType),
    idImprovement = rs.get(a.idImprovement),
    signature = rs.get(a.signature),
    name = rs.get(a.name),
    description = rs.get(a.description),
    createdDate = rs.get(a.createdDate),
    updatedDate = rs.get(a.updatedDate)
  )

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Assignment as a).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
              idImprType: Int,
              idImprovement: Int,
              signature: String,
              name: String,
              description: Option[String] = None,
              createdDate: ZonedDateTime,
              updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): Assignment = {
    val generatedKey = withSQL {
      insert.into(Assignment).namedValues(
        column.idImprType -> idImprType,
        column.idImprovement -> idImprovement,
        column.signature -> signature,
        column.name -> name,
        column.description -> description,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    Assignment(
      id = generatedKey.toInt,
      idImprType = idImprType,
      idImprovement = idImprovement,
      signature = signature,
      name = name,
      description = description,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[Assignment])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idImprType -> entity.idImprType,
        'idImprovement -> entity.idImprovement,
        'signature -> entity.signature,
        'name -> entity.name,
        'description -> entity.description,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL(
      """insert into assignment(
      id_impr_type,
      id_improvement,
      signature,
      name,
      description,
      created_date,
      updated_date
    ) values (
      {idImprType},
      {idImprovement},
      {signature},
      {name},
      {description},
      {createdDate},
      {updatedDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Assignment)(implicit session: DBSession = autoSession): Assignment = {
    withSQL {
      update(Assignment).set(
        column.id -> entity.id,
        column.idImprType -> entity.idImprType,
        column.idImprovement -> entity.idImprovement,
        column.signature -> entity.signature,
        column.name -> entity.name,
        column.description -> entity.description,
        column.createdDate -> entity.createdDate,
        column.updatedDate -> entity.updatedDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Assignment)(implicit session: DBSession = autoSession): Int = {
    withSQL {
      delete.from(Assignment).where.eq(column.id, entity.id)
    }.update.apply()
  }

}
