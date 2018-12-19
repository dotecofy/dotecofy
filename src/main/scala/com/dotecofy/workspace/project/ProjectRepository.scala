package com.dotecofy.workspace.project

import com.dotecofy.exception.Error
import com.dotecofy.models._
import scalikejdbc._

import scala.util.Right

trait ProjectRepositoryComponent {

  def create(project: Project)

  def update(signature: String, project: Project)

  def delete(signature: String)

  def findByWorkspace(user: User, workspaceSig: String, index: Int, nb: Int): Either[Error, List[Project]]

  def hasRight(user: User, project: Project): Boolean

}

object ProjectRepository extends ProjectRepositoryComponent with SQLSyntaxSupport[Project] {

  val p = Project.syntax("p")

  override def create(project: Project): Unit = ???

  override def update(signature: String, project: Project): Unit = ???

  override def delete(signature: String): Unit = ???

  override def findByWorkspace(user: User, workspaceSig: String, index: Int, nb: Int): Either[Error, List[Project]] = {
    findByWorkspaceImpl(user, workspaceSig, index, nb)
  }

  override def hasRight(user: User, project: Project): Boolean = ???

  private def findByWorkspaceImpl(user: User, workspaceSig: String, index: Int, nb: Int)(implicit session: DBSession = autoSession): Either[Error, List[Project]] = {
    val userId = user.id
    val projects: List[Project] = sql"select ${p.result.*} from ${Project.as(p)} inner join workspace on ${p.idWorkspace}=workspace.id where workspace.signature=${workspaceSig} and ${p.id} in(select id_tuple from group_right inner join `right` on group_right.id_right=right.id inner join user_group on group_right.id_group=user_group.id_group where user_group.id_user=${userId} and right.right='VIEW_PROJECT')"
      .map(Project(p.resultName)).list.apply()
    Right(projects)
  }

}
