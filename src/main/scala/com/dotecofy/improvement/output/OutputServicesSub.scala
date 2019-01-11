
package com.dotecofy.improvement.output

sealed trait OutputServicesComponentSub extends OutputServicesComponent {

  implicit val repository: OutputRepositoryComponentSub = OutputRepositorySub

}

object OutputServicesSub extends OutputServices with OutputServicesComponentSub {

}