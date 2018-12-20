package com.dotecofy.context.layer

import com.dotecofy.exception.Error
import com.dotecofy.models.{Layer, User}

trait LayerServicesComponent {

  val SIGNATURE_MAX_LENGTH = 80
  val SIGNATURE_MIN_LENGTH = 3

  val NAME_MAX_LENGTH = 50
  val NAME_MIN_LENGTH = 3

  val DESCRIPTION_MAX_LENGTH = 200

  def create(user: User, workspaceSign: String, layer: Layer)(implicit repository: LayerRepositoryComponent): Either[Error, Layer]

  def update(user: User, signature: String, layer: Layer)(implicit repository: LayerRepositoryComponent): Either[Error, Layer]

  def delete(user: User, signature: String)(implicit repository: LayerRepositoryComponent): Either[Error, Unit]

  def findByWorkspace(user: User, workspaceSign: String, index: Int, nb: Int)(implicit repository: LayerRepositoryComponent): Either[Error, List[Layer]]
}

object LayerServices extends LayerServicesComponent {

  override def create(user: User, workspaceSign: String, layer: Layer)(implicit repository: LayerRepositoryComponent): Either[Error, Layer] = ???

  override def update(user: User, signature: String, layer: Layer)(implicit repository: LayerRepositoryComponent): Either[Error, Layer] = ???

  override def delete(user: User, signature: String)(implicit repository: LayerRepositoryComponent): Either[Error, Unit] = ???

  override def findByWorkspace(user: User, workspaceSign: String, index: Int, nb: Int)(implicit repository: LayerRepositoryComponent): Either[Error, List[Layer]] = ???
}
