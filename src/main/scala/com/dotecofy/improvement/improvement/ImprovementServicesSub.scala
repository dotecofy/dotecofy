
package com.dotecofy.improvement.improvement

sealed trait ImprovementServicesComponentSub extends ImprovementServicesComponent {

  implicit val repository: ImprovementRepositoryComponentSub = ImprovementRepositorySub

}

object ImprovementServicesSub extends ImprovementServices with ImprovementServicesComponentSub {

}