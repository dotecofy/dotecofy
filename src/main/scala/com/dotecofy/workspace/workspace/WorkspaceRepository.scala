package com.dotecofy.workspace.workspace

import scalikejdbc._
import com.dotecofy.models._

trait WorkspaceRepositoryComponent {

  def create(workspace: Workspace)

  def update(signature: String, workspace: Workspace)

  def delete(signature: String)

  def findByUser(user: User, index: Int, nb: Int): List[Workspace]

  def hasRight(user: User, workspace: Workspace): Boolean

}

object WorkspaceRepository extends WorkspaceRepositoryComponent with SQLSyntaxSupport[Workspace] {

  val w = Workspace.syntax("w")

  override def create(workspace: Workspace): Unit = {

  }

  override def update(signature: String, workspace: Workspace): Unit = ???

  override def delete(signature: String): Unit = ???

  override def findByUser(user: User, index: Int, nb: Int): List[Workspace] = {
    findByUserImpl(user)
  }


  override def hasRight(user: User, workspace: Workspace): Boolean = {
    hasRightImpl(user, workspace)
  }

  private def findByUserImpl(user: User)(implicit session: DBSession = autoSession): List[Workspace] = {
    val userId = user.id
    val workspaces:List[Workspace] = sql"select ${w.result.*} from ${Workspace.as(w)} where id in(select id_tuple from group_right inner join `right` on group_right.id_right=right.id inner join user_group on group_right.id_group=user_group.id_group where user_group.id_user=${userId} and right.right='VIEW_WORKSPACE')"
      //.map(rs => Workspace(rs)).list.apply()
      .map(Workspace(w.resultName)).list.apply()
    workspaces
  }

  private def hasRightImpl(user: User, workspace: Workspace)(implicit session: DBSession = autoSession): Boolean = {
    SQL("select count(id) from group_right inner join 'right' on group_right.id_right='right'.id inner join user_group on group_right.id_group=user_group.id_group where user_group.id_user={userId} and right.id_tuple={workspaceId} and right.right='VIEW_WORKSPACE'").bindByName('userId -> user.id).bindByName('workspaceId -> workspace.id)
      .map(_.int(1)).single.apply().get > 0
  }
}
