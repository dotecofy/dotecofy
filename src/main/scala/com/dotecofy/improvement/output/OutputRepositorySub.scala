
package com.dotecofy.improvement.output

sealed trait OutputRepositoryComponentSub extends OutputRepositoryComponent

object OutputRepositorySub extends OutputRepository with OutputRepositoryComponentSub {

}