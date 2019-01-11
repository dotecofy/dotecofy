
package com.dotecofy.workspace.workspace

sealed trait WorkspaceRepositoryComponentSub extends WorkspaceRepositoryComponent

object WorkspaceRepositorySub extends WorkspaceRepository with WorkspaceRepositoryComponentSub {

}