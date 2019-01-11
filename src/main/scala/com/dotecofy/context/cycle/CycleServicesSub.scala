
package com.dotecofy.context.cycle

sealed trait CycleServicesComponentSub extends CycleServicesComponent {

  implicit val repository: CycleRepositoryComponentSub = CycleRepositorySub

}

object CycleServicesSub extends CycleServices with CycleServicesComponentSub {

}