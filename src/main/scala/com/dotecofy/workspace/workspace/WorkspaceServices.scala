package com.dotecofy.workspace.workspace

import com.dotecofy.models.{User, Workspace}

trait WorkspaceServicesComponent {

  val SIGNATURE_MAX_LENGTH = 80
  val SIGNATURE_MIN_LENGTH = 3

  val NAME_MAX_LENGTH = 50
  val NAME_MIN_LENGTH = 3

  val DESCRIPTION_MAX_LENGTH = 200

  def create(user: User, workspace: Workspace)

  def update(user: User, signature: String, workspace: Workspace)

  def delete(user: User, signature: String)

  def findByUser(user: User, index: Int, nb: Int)(implicit repository:WorkspaceRepositoryComponent): List[Workspace]

}

object WorkspaceServices extends WorkspaceServicesComponent {

  override def create(user: User, workspace: Workspace): Unit = {

  }

  override def findByUser(user: User, index: Int, nb: Int)(implicit repository:WorkspaceRepositoryComponent): List[Workspace] = {
    repository.findByUser(user, index, nb)
  }

  override def update(user: User, signature: String, workspace: Workspace): Unit = ???

  override def delete(user: User, signature: String): Unit = ???

  private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

  private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

  private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

  private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

  private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

  private def hasRight(user:User, workspace:Workspace): Boolean = {
    WorkspaceRepository.hasRight(user, workspace)
  }
}
