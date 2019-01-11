
package com.dotecofy.workspace.feature

sealed trait FeatureRepositoryComponentSub extends FeatureRepositoryComponent

object FeatureRepositorySub extends FeatureRepository with FeatureRepositoryComponentSub {

}