package com.dotecofy.models

import scalikejdbc._

case class Right(
  id: Int,
  right: String,
  table: Option[String] = None) {

  def save()(implicit session: DBSession = Right.autoSession): Right = Right.save(this)(session)

  def destroy()(implicit session: DBSession = Right.autoSession): Int = Right.destroy(this)(session)

}


object Right extends SQLSyntaxSupport[Right] {

  override val schemaName = Some("dotecofy")

  override val tableName = "right"

  override val columns = Seq("id", "right", "table")

  def apply(r: SyntaxProvider[Right])(rs: WrappedResultSet): Right = apply(r.resultName)(rs)
  def apply(r: ResultName[Right])(rs: WrappedResultSet): Right = new Right(
    id = rs.get(r.id),
    right = rs.get(r.right),
    table = rs.get(r.table)
  )

  val r = Right.syntax("r")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Right] = {
    withSQL {
      select.from(Right as r).where.eq(r.id, id)
    }.map(Right(r.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Right] = {
    withSQL(select.from(Right as r)).map(Right(r.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Right as r)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Right] = {
    withSQL {
      select.from(Right as r).where.append(where)
    }.map(Right(r.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Right] = {
    withSQL {
      select.from(Right as r).where.append(where)
    }.map(Right(r.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Right as r).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    right: String,
    table: Option[String] = None)(implicit session: DBSession = autoSession): Right = {
    val generatedKey = withSQL {
      insert.into(Right).namedValues(
        column.right -> right,
        column.table -> table
      )
    }.updateAndReturnGeneratedKey.apply()

    Right(
      id = generatedKey.toInt,
      right = right,
      table = table)
  }

  def batchInsert(entities: collection.Seq[Right])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'right -> entity.right,
        'table -> entity.table))
    SQL("""insert into right(
      right,
      table
    ) values (
      {right},
      {table}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Right)(implicit session: DBSession = autoSession): Right = {
    withSQL {
      update(Right).set(
        column.id -> entity.id,
        column.right -> entity.right,
        column.table -> entity.table
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Right)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Right).where.eq(column.id, entity.id) }.update.apply()
  }

}
