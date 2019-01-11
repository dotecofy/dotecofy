
package com.dotecofy.context.layer

sealed trait LayerServicesComponentSub extends LayerServicesComponent {

  implicit val repository: LayerRepositoryComponentSub = LayerRepositorySub

}

object LayerServicesSub extends LayerServices with LayerServicesComponentSub {

}