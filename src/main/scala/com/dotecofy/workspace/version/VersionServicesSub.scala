
package com.dotecofy.workspace.version

sealed trait VersionServicesComponentSub extends VersionServicesComponent {

  implicit val repository: VersionRepositoryComponentSub = VersionRepositorySub

}

object VersionServicesSub extends VersionServices with VersionServicesComponentSub {

}