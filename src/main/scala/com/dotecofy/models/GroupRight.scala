package com.dotecofy.models

import scalikejdbc._

case class GroupRight(
  id: Int,
  idGroup: Int,
  idRight: Int,
  idTuple: Int) {

  def save()(implicit session: DBSession = GroupRight.autoSession): GroupRight = GroupRight.save(this)(session)

  def destroy()(implicit session: DBSession = GroupRight.autoSession): Int = GroupRight.destroy(this)(session)

}


object GroupRight extends SQLSyntaxSupport[GroupRight] {

  override val schemaName = Some("dotecofy")

  override val tableName = "group_right"

  override val columns = Seq("id", "id_group", "id_right", "id_tuple")

  def apply(gr: SyntaxProvider[GroupRight])(rs: WrappedResultSet): GroupRight = apply(gr.resultName)(rs)
  def apply(gr: ResultName[GroupRight])(rs: WrappedResultSet): GroupRight = new GroupRight(
    id = rs.get(gr.id),
    idGroup = rs.get(gr.idGroup),
    idRight = rs.get(gr.idRight),
    idTuple = rs.get(gr.idTuple)
  )

  val gr = GroupRight.syntax("gr")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[GroupRight] = {
    withSQL {
      select.from(GroupRight as gr).where.eq(gr.id, id)
    }.map(GroupRight(gr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[GroupRight] = {
    withSQL(select.from(GroupRight as gr)).map(GroupRight(gr.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(GroupRight as gr)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[GroupRight] = {
    withSQL {
      select.from(GroupRight as gr).where.append(where)
    }.map(GroupRight(gr.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[GroupRight] = {
    withSQL {
      select.from(GroupRight as gr).where.append(where)
    }.map(GroupRight(gr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(GroupRight as gr).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    idGroup: Int,
    idRight: Int,
    idTuple: Int)(implicit session: DBSession = autoSession): GroupRight = {
    val generatedKey = withSQL {
      insert.into(GroupRight).namedValues(
        column.idGroup -> idGroup,
        column.idRight -> idRight,
        column.idTuple -> idTuple
      )
    }.updateAndReturnGeneratedKey.apply()

    GroupRight(
      id = generatedKey.toInt,
      idGroup = idGroup,
      idRight = idRight,
      idTuple = idTuple)
  }

  def batchInsert(entities: collection.Seq[GroupRight])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'idGroup -> entity.idGroup,
        'idRight -> entity.idRight,
        'idTuple -> entity.idTuple))
    SQL("""insert into group_right(
      id_group,
      id_right,
      id_tuple
    ) values (
      {idGroup},
      {idRight},
      {idTuple}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: GroupRight)(implicit session: DBSession = autoSession): GroupRight = {
    withSQL {
      update(GroupRight).set(
        column.id -> entity.id,
        column.idGroup -> entity.idGroup,
        column.idRight -> entity.idRight,
        column.idTuple -> entity.idTuple
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: GroupRight)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(GroupRight).where.eq(column.id, entity.id) }.update.apply()
  }

}
