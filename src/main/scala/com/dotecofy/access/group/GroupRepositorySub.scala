
package com.dotecofy.access.group

sealed trait GroupRepositoryComponentSub extends GroupRepositoryComponent

object GroupRepositorySub extends GroupRepository with GroupRepositoryComponentSub {

}