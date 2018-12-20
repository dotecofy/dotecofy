package com.dotecofy.workspace.project

import com.dotecofy.exception.{Error, ErrorBuilder}
import com.dotecofy.models.{Project, User}

import scala.collection.mutable.ArrayBuffer

trait ProjectServicesComponent {

  val ERROR_INVALID_PROJECT_FORM = "invalid_project_form"

  val SIGNATURE_MAX_LENGTH = 80
  val SIGNATURE_MIN_LENGTH = 3

  val NAME_MAX_LENGTH = 50
  val NAME_MIN_LENGTH = 3

  val DESCRIPTION_MAX_LENGTH = 200

  def create(user: User, workspaceSig: String, project: Project)(implicit repository: ProjectRepositoryComponent): Either[Error, Project]

  def update(user: User, projectSig: String, project: Project)(implicit repository: ProjectRepositoryComponent): Either[Error, Project]

  def delete(user: User, signature: String)(implicit repository: ProjectRepositoryComponent): Either[Error, Unit]

  def findByWorkspace(user: User, workspaceSign: String, index: Int, nb: Int)(implicit repository: ProjectRepositoryComponent): Either[Error, List[Project]]

}

object ProjectServices extends ProjectServicesComponent {

  override def create(user: User, workspaceSig: String, project: Project)(implicit repository: ProjectRepositoryComponent): Either[Error, Project] = {

    val fieldErrors = ArrayBuffer.empty[Error]
    if (isSignatureTooLong(project.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature is too long"))
    if (isSignatureTooShort(project.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature is too short"))
    if (isNameTooLong(project.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name is too long"))
    if (isNameTooShort(project.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name is too short"))
    if (isDescriptionTooLong(project.description.get)) fieldErrors.append(ErrorBuilder.fieldError("description", "description is too long"))
    if (!fieldErrors.isEmpty) {
      return Left(ErrorBuilder.invalidForm(ERROR_INVALID_PROJECT_FORM, "Please verify the form", fieldErrors.toList))
    }

    Right(null)
  }

  override def update(user: User, projectSig: String, project: Project)(implicit repository: ProjectRepositoryComponent): Either[Error, Project] = ???

  override def delete(user: User, signature: String)(implicit repository: ProjectRepositoryComponent): Either[Error, Unit] = ???

  override def findByWorkspace(user: User, workspaceSign: String, index: Int, nb: Int)(implicit repository: ProjectRepositoryComponent): Either[Error, List[Project]] = {
    repository.findByWorkspace(user, workspaceSign, index, nb)
  }

  private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

  private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

  private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

  private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

  private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

}
