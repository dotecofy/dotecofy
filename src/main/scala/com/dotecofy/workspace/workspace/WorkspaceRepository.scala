package com.dotecofy.workspace.workspace

import java.time.ZonedDateTime

import com.dotecofy.exception.{Error, ErrorBuilder}
import com.dotecofy.workspace.workspace.WorkspaceRepository.autoSession
import scalikejdbc._

trait WorkspaceRepositoryComponent {

  val ERROR_COULD_NOT_INSERT = "could_not_insert"
  val ERROR_COULD_NOT_UPDATE = "could_not_update"

  def create(workspace: WorkspaceDB)(implicit session: DBSession = autoSession): Either[Error, WorkspaceDB]

  def update(signature: String, workspace: WorkspaceDB)(implicit session: DBSession = autoSession): Either[Error, WorkspaceDB]

  def delete(signature: String)(implicit session: DBSession = autoSession): Either[Error, Unit]

  def findByUser(userId: Int, index: Int, nb: Int)(implicit session: DBSession = autoSession): Either[Error, List[WorkspaceDB]]

  def findIfAllowed(userId: Int, workspaceSign: String)(implicit session: DBSession = autoSession): Either[Error, Option[WorkspaceDB]]

}

object WorkspaceRepository extends WorkspaceRepositoryComponent with SQLSyntaxSupport[WorkspaceDB] {

  override val schemaName = Some("dotecofy")

  override val tableName = "workspace"

  override val columns = Seq("id", "signature", "name", "description", "created_date", "updated_date")

  val w = WorkspaceRepository.syntax("w")

  //override val columns = Seq("id", "signature", "name", "description", "created_date", "updated_date")

  override val autoSession = AutoSession

  override def create(workspace: WorkspaceDB)(implicit session: DBSession = autoSession): Either[Error, WorkspaceDB] = {
    val generatedKey = withSQL {
      insert.into(WorkspaceRepository).namedValues(
        column.signature -> workspace.signature,
        column.name -> workspace.name,
        column.description -> workspace.description,
        column.createdDate -> ZonedDateTime.now
      )
    }.updateAndReturnGeneratedKey.apply()

    val findW: Option[WorkspaceDB] = find(generatedKey.toInt)
    findW match {
      case Some(ret) => Right(ret)
      case None => Left(ErrorBuilder.internalError(ERROR_COULD_NOT_INSERT, "The generated key is not found in database"))
    }

  }

  override def update(signature: String, workspace: WorkspaceDB)(implicit session: DBSession = autoSession): Either[Error, WorkspaceDB] = {
    withSQL {
      scalikejdbc.update(WorkspaceRepository).set(
        column.signature -> workspace.signature,
        column.name -> workspace.name,
        column.description -> workspace.description,
        column.updatedDate -> ZonedDateTime.now
      ).where.eq(column.signature, signature)
    }.update.apply()

    val findW: Option[WorkspaceDB] = findBySignature(workspace.signature)
    findW match {
      case Some(ret) => Right(ret)
      case None => Left(ErrorBuilder.internalError(ERROR_COULD_NOT_INSERT, "The id is not found in database"))
    }
  }

  override def delete(signature: String)(implicit session: DBSession): Either[Error, Unit] = {
    withSQL {
      scalikejdbc.delete.from(WorkspaceRepository).where.eq(column.signature, signature)
    }.update.apply()
    Right()
  }

  override def findByUser(userId: Int, index: Int, nb: Int)(implicit session: DBSession): Either[Error, List[WorkspaceDB]] = {
    val workspaces: List[WorkspaceDB] = sql"select ${w.result.*} from ${WorkspaceRepository.as(w)} where id in(select id_tuple from group_right inner join `right` on group_right.id_right=right.id inner join user_group on group_right.id_group=user_group.id_group where user_group.id_user=${userId} and right.right='VIEW_WORKSPACE')"
      //.map(rs => WorkspaceDB(rs)).list.apply()
      .map(WorkspaceRepository(w.resultName)).list.apply()
    Right(workspaces)
  }

  override def findIfAllowed(userId: Int, signature: String)(implicit session: DBSession): Either[Error, Option[WorkspaceDB]] = {
    val workspace: Option[WorkspaceDB] = sql"select ${w.result.*} from ${WorkspaceRepository.as(w)} inner join group_right on ${w.id} = group_right.id_tuple inner join user_group on group_right.id_group = user_group.id_group inner join `right` on group_right.id_right=right.id where user_group.id_user=${userId} and ${w.signature}=${signature} and right.right='VIEW_WORKSPACE';"
      .map(WorkspaceRepository(w.resultName)).single.apply()

    Right(workspace)
    /*SQL("select * ${w.result.*} from ${WorkspaceRepository.as(w)}  group_right inner join 'right' on group_right.id_right='right'.id inner join user_group on group_right.id_group=user_group.id_group where user_group.id_user={userId} and right.id_tuple={workspaceId} and right.right='VIEW_WORKSPACE'").bindByName('userId -> user.id).bindByName('workspaceId -> workspace.id)
      .map(_.int(1)).single.apply().get > 0*/
  }

  private def find(id: Int)(implicit session: DBSession = autoSession): Option[WorkspaceDB] = {
    withSQL {
      select.from(WorkspaceRepository as w).where.eq(w.id, id)
    }.map(WorkspaceRepository(w.resultName)).single.apply()
  }

  private def findBySignature(signature: String)(implicit session: DBSession = autoSession): Option[WorkspaceDB] = {
    withSQL {
      select.from(WorkspaceRepository as w).where.eq(w.signature, signature)
    }.map(WorkspaceRepository(w.resultName)).single.apply()
  }

  def apply(w: SyntaxProvider[WorkspaceDB])(rs: WrappedResultSet): WorkspaceDB = apply(w.resultName)(rs)
  def apply(w: ResultName[WorkspaceDB])(rs: WrappedResultSet): WorkspaceDB = new WorkspaceDB(
    id = rs.get(w.id),
    signature = rs.get(w.signature),
    name = rs.get(w.name),
    description = rs.get(w.description),
    createdDate = rs.get(w.createdDate),
    updatedDate = rs.get(w.updatedDate)
  )
}
