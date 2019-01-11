
package com.dotecofy.workspace.version

sealed trait VersionRepositoryComponentSub extends VersionRepositoryComponent

object VersionRepositorySub extends VersionRepository with VersionRepositoryComponentSub {

}