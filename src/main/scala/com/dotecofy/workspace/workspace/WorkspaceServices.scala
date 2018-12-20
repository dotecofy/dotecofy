package com.dotecofy.workspace.workspace

import com.dotecofy.exception.{Error, ErrorBuilder}
import com.dotecofy.models.User

import scala.collection.mutable.ArrayBuffer

trait WorkspaceServicesComponent {

  val ERROR_INVALID_WORKSPACE_FORM = "invalid_workspace_form"

  val SIGNATURE_MAX_LENGTH = 80
  val SIGNATURE_MIN_LENGTH = 3

  val NAME_MAX_LENGTH = 50
  val NAME_MIN_LENGTH = 3

  val DESCRIPTION_MAX_LENGTH = 200

  def create(user: User, workspace: WorkspaceSrv)(implicit repository: WorkspaceRepositoryComponent): Either[Error, WorkspaceSrv]

  def update(user: User, signature: String, workspace: WorkspaceSrv)(implicit repository: WorkspaceRepositoryComponent): Either[Error, WorkspaceSrv]

  def delete(user: User, signature: String)(implicit repository: WorkspaceRepositoryComponent): Either[Error, Unit]

  def findByUser(user: User, index: Int, nb: Int)(implicit repository: WorkspaceRepositoryComponent): Either[Error, List[WorkspaceSrv]]

}

object WorkspaceServices extends WorkspaceServicesComponent {

  override def create(user: User, workspace: WorkspaceSrv)(implicit repository: WorkspaceRepositoryComponent): Either[Error, WorkspaceSrv] = {

    val fieldErrors = ArrayBuffer.empty[Error]
    if (isSignatureTooLong(workspace.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature is too long"))
    if (isSignatureTooShort(workspace.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature is too short"))
    if (isNameTooLong(workspace.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name is too long"))
    if (isNameTooShort(workspace.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name is too short"))
    if (isDescriptionTooLong(workspace.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description is too long"))
    if (!fieldErrors.isEmpty) {
      return Left(ErrorBuilder.invalidForm(ERROR_INVALID_WORKSPACE_FORM, "Please verify the form", fieldErrors.toList))
    }

    val workspaceDB = WorkspaceDB(
      0,
      workspace.signature,
      workspace.name,
      Option(workspace.description),
      null, None
    )
    val resp = repository.create(workspaceDB)
    resp match {
      case Left(ret) => Left(ret)
      case Right(ret) => Right(dbToSrv(ret))
    }
  }

  override def update(user: User, signature: String, workspace: WorkspaceSrv)(implicit repository: WorkspaceRepositoryComponent): Either[Error, WorkspaceSrv] = ???

  override def delete(user: User, signature: String)(implicit repository: WorkspaceRepositoryComponent): Either[Error, Unit] = ???

  override def findByUser(user: User, index: Int, nb: Int)(implicit repository: WorkspaceRepositoryComponent): Either[Error, List[WorkspaceSrv]] = {
    repository.findByUser(user.id, index, nb) match {
      case Left(error) => Left(error)
      case Right(workspaces) => Right(workspaces.map(ws => dbToSrv(ws)))
    }
  }

  private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

  private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

  private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

  private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

  private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

  private def dbToSrv(workspace: WorkspaceDB): WorkspaceSrv = {
    WorkspaceSrv(workspace.signature, workspace.name, workspace.description.getOrElse(""), workspace.createdDate, workspace.updatedDate.getOrElse(null))
  }
}
