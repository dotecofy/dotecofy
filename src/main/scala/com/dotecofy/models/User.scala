package com.dotecofy.models

import scalikejdbc._
import java.time.{ZonedDateTime}

case class User(
  id: Int,
  fullname: String,
  email: String,
  salt: String,
  password: String,
  createdDate: ZonedDateTime,
  updatedDate: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = User.autoSession): User = User.save(this)(session)

  def destroy()(implicit session: DBSession = User.autoSession): Int = User.destroy(this)(session)

}


object User extends SQLSyntaxSupport[User] {

  override val schemaName = Some("dotecofy")

  override val tableName = "user"

  override val columns = Seq("id", "fullname", "email", "salt", "password", "created_date", "updated_date")

  def apply(u: SyntaxProvider[User])(rs: WrappedResultSet): User = apply(u.resultName)(rs)
  def apply(u: ResultName[User])(rs: WrappedResultSet): User = new User(
    id = rs.get(u.id),
    fullname = rs.get(u.fullname),
    email = rs.get(u.email),
    salt = rs.get(u.salt),
    password = rs.get(u.password),
    createdDate = rs.get(u.createdDate),
    updatedDate = rs.get(u.updatedDate)
  )

  val u = User.syntax("u")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[User] = {
    withSQL {
      select.from(User as u).where.eq(u.id, id)
    }.map(User(u.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[User] = {
    withSQL(select.from(User as u)).map(User(u.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(User as u)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[User] = {
    withSQL {
      select.from(User as u).where.append(where)
    }.map(User(u.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[User] = {
    withSQL {
      select.from(User as u).where.append(where)
    }.map(User(u.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(User as u).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    fullname: String,
    email: String,
    salt: String,
    password: String,
    createdDate: ZonedDateTime,
    updatedDate: Option[ZonedDateTime] = None)(implicit session: DBSession = autoSession): User = {
    val generatedKey = withSQL {
      insert.into(User).namedValues(
        column.fullname -> fullname,
        column.email -> email,
        column.salt -> salt,
        column.password -> password,
        column.createdDate -> createdDate,
        column.updatedDate -> updatedDate
      )
    }.updateAndReturnGeneratedKey.apply()

    User(
      id = generatedKey.toInt,
      fullname = fullname,
      email = email,
      salt = salt,
      password = password,
      createdDate = createdDate,
      updatedDate = updatedDate)
  }

  def batchInsert(entities: collection.Seq[User])(implicit session: DBSession = autoSession): List[Int] = {
    val params: collection.Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'fullname -> entity.fullname,
        'email -> entity.email,
        'salt -> entity.salt,
        'password -> entity.password,
        'createdDate -> entity.createdDate,
        'updatedDate -> entity.updatedDate))
    SQL("""insert into user(
      fullname,
      email,
      salt,
      password,
      created_date,
      updated_date
    ) values (
      {fullname},
      {email},
      {salt},
      {password},
      {createdDate},
      {updatedDate}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: User)(implicit session: DBSession = autoSession): User = {
    withSQL {
      update(User).set(
        column.id -> entity.id,
        column.fullname -> entity.fullname,
        column.email -> entity.email,
        column.salt -> entity.salt,
        column.password -> entity.password,
        column.createdDate -> entity.createdDate,
        column.updatedDate -> entity.updatedDate
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: User)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(User).where.eq(column.id, entity.id) }.update.apply()
  }

}
