package com.dotecofy.context.cycle

import com.dotecofy.exception.Error
import com.dotecofy.models.{Cycle, User}

trait CycleServicesComponent {

  val SIGNATURE_MAX_LENGTH = 80
  val SIGNATURE_MIN_LENGTH = 3

  val NAME_MAX_LENGTH = 50
  val NAME_MIN_LENGTH = 3

  val DESCRIPTION_MAX_LENGTH = 200

  def create(user: User, workspaceSign: String, cycle: Cycle)(implicit repository: CycleRepositoryComponent): Either[Error, Cycle]

  def update(user: User, signature: String, cycle: Cycle)(implicit repository: CycleRepositoryComponent): Either[Error, Cycle]

  def delete(user: User, signature: String)(implicit repository: CycleRepositoryComponent): Either[Error, Unit]

  def findByWorkspace(user: User, workspaceSign: String, index: Int, nb: Int)(implicit repository: CycleRepositoryComponent): Either[Error, List[Cycle]]
}

object CycleServices extends CycleServicesComponent {

  override def create(user: User, workspaceSign: String, cycle: Cycle)(implicit repository: CycleRepositoryComponent): Either[Error, Cycle] = ???

  override def update(user: User, signature: String, cycle: Cycle)(implicit repository: CycleRepositoryComponent): Either[Error, Cycle] = ???

  override def delete(user: User, signature: String)(implicit repository: CycleRepositoryComponent): Either[Error, Unit] = ???

  override def findByWorkspace(user: User, workspaceSign: String, index: Int, nb: Int)(implicit repository: CycleRepositoryComponent): Either[Error, List[Cycle]] = ???
}
