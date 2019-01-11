
package com.dotecofy.workspace.workspace

sealed trait WorkspaceServicesComponentSub extends WorkspaceServicesComponent {

  implicit val repository: WorkspaceRepositoryComponentSub = WorkspaceRepositorySub

}

object WorkspaceServicesSub extends WorkspaceServices with WorkspaceServicesComponentSub {

}