
package com.dotecofy.context.layer

sealed trait LayerRepositoryComponentSub extends LayerRepositoryComponent

object LayerRepositorySub extends LayerRepository with LayerRepositoryComponentSub {

}