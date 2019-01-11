
package com.dotecofy.workspace.feature

sealed trait FeatureServicesComponentSub extends FeatureServicesComponent {

  implicit val repository: FeatureRepositoryComponentSub = FeatureRepositorySub

}

object FeatureServicesSub extends FeatureServices with FeatureServicesComponentSub {

}