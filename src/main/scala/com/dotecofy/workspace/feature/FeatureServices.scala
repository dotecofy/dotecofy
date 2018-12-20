package com.dotecofy.workspace.feature

import com.dotecofy.exception.Error
import com.dotecofy.models.{Feature, User}

trait FeatureServicesComponent {

  val SIGNATURE_MAX_LENGTH = 80
  val SIGNATURE_MIN_LENGTH = 3

  val NAME_MAX_LENGTH = 50
  val NAME_MIN_LENGTH = 3

  val DESCRIPTION_MAX_LENGTH = 200

  def create(user: User, projectSign: String, feature: Feature)(implicit repository: FeatureRepositoryComponent): Either[Error, Feature]

  def update(user: User, signature: String, feature: Feature)(implicit repository: FeatureRepositoryComponent): Either[Error, Feature]

  def delete(user: User, signature: String)(implicit repository: FeatureRepositoryComponent): Either[Error, Unit]

  def findByProject(user: User, projectSign: String, index: Int, nb: Int)(implicit repository: FeatureRepositoryComponent): Either[Error, List[Feature]]
}

object FeatureServices extends FeatureServicesComponent {

  override def create(user: User, projectSign: String, feature: Feature)(implicit repository: FeatureRepositoryComponent): Either[Error, Feature] = ???

  override def update(user: User, signature: String, feature: Feature)(implicit repository: FeatureRepositoryComponent): Either[Error, Feature] = ???

  override def delete(user: User, signature: String)(implicit repository: FeatureRepositoryComponent): Either[Error, Unit] = ???

  override def findByProject(user: User, projectSign: String, index: Int, nb: Int)(implicit repository: FeatureRepositoryComponent): Either[Error, List[Feature]] = ???
}
