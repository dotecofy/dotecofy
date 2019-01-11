
package com.dotecofy.context.cycle

sealed trait CycleRepositoryComponentSub extends CycleRepositoryComponent

object CycleRepositorySub extends CycleRepository with CycleRepositoryComponentSub {

}