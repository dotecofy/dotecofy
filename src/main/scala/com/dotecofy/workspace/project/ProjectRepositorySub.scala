
package com.dotecofy.workspace.project

sealed trait ProjectRepositoryComponentSub extends ProjectRepositoryComponent

object ProjectRepositorySub extends ProjectRepository with ProjectRepositoryComponentSub {

}