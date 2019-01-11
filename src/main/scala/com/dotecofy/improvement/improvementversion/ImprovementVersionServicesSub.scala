
package com.dotecofy.improvement.improvementversion

sealed trait ImprovementVersionServicesComponentSub extends ImprovementVersionServicesComponent {

  implicit val repository: ImprovementVersionRepositoryComponentSub = ImprovementVersionRepositorySub

}

object ImprovementVersionServicesSub extends ImprovementVersionServices with ImprovementVersionServicesComponentSub {

}