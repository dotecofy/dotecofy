
package com.dotecofy.access.group

sealed trait GroupServicesComponentSub extends GroupServicesComponent {

  implicit val repository: GroupRepositoryComponentSub = GroupRepositorySub

}

object GroupServicesSub extends GroupServices with GroupServicesComponentSub {

}