
package com.dotecofy.improvement.improvementcycle

sealed trait ImprovementCycleServicesComponentSub extends ImprovementCycleServicesComponent {

  implicit val repository: ImprovementCycleRepositoryComponentSub = ImprovementCycleRepositorySub

}

object ImprovementCycleServicesSub extends ImprovementCycleServices with ImprovementCycleServicesComponentSub {

}