
package com.dotecofy.improvement.improvementlayer

sealed trait ImprovementLayerServicesComponentSub extends ImprovementLayerServicesComponent {

  implicit val repository: ImprovementLayerRepositoryComponentSub = ImprovementLayerRepositorySub

}

object ImprovementLayerServicesSub extends ImprovementLayerServices with ImprovementLayerServicesComponentSub {

}