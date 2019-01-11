
package com.dotecofy.access.user

sealed trait UserServicesComponentSub extends UserServicesComponent {

  implicit val repository: UserRepositoryComponentSub = UserRepositorySub

}

object UserServicesSub extends UserServices with UserServicesComponentSub {

}