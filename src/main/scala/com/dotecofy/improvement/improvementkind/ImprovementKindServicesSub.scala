
package com.dotecofy.improvement.improvementkind

sealed trait ImprovementKindServicesComponentSub extends ImprovementKindServicesComponent {

  implicit val repository: ImprovementKindRepositoryComponentSub = ImprovementKindRepositorySub

}

object ImprovementKindServicesSub extends ImprovementKindServices with ImprovementKindServicesComponentSub {

}