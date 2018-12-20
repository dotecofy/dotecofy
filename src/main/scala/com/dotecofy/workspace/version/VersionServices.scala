package com.dotecofy.workspace.feature

import com.dotecofy.exception.Error
import com.dotecofy.models.{User, Version}
import com.dotecofy.workspace.version.VersionRepositoryComponent


trait VersionServicesComponent {

  val SIGNATURE_MAX_LENGTH = 80
  val SIGNATURE_MIN_LENGTH = 3

  val NAME_MAX_LENGTH = 50
  val NAME_MIN_LENGTH = 3

  val DESCRIPTION_MAX_LENGTH = 200

  def create(user: User, projectSign: String, version: Version)(implicit repository: VersionRepositoryComponent): Either[Error, Version]

  def update(user: User, signature: String, version: Version)(implicit repository: VersionRepositoryComponent): Either[Error, Version]

  def delete(user: User, signature: String)(implicit repository: VersionRepositoryComponent): Either[Error, Unit]

  def findByProject(user: User, projectSign: String, index: Int, nb: Int)(implicit repository: VersionRepositoryComponent): Either[Error, List[Version]]
}

object VersionServices extends VersionServicesComponent {

  override def create(user: User, projectSign: String, version: Version)(implicit repository: VersionRepositoryComponent): Either[Error, Version] = ???

  override def update(user: User, signature: String, version: Version)(implicit repository: VersionRepositoryComponent): Either[Error, Version] = ???

  override def delete(user: User, signature: String)(implicit repository: VersionRepositoryComponent): Either[Error, Unit] = ???

  override def findByProject(user: User, projectSign: String, index: Int, nb: Int)(implicit repository: VersionRepositoryComponent): Either[Error, List[Version]] = ???
}
