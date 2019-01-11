
package com.dotecofy.workspace.project

sealed trait ProjectServicesComponentSub extends ProjectServicesComponent {

  implicit val repository: ProjectRepositoryComponentSub = ProjectRepositorySub

}

object ProjectServicesSub extends ProjectServices with ProjectServicesComponentSub {

}