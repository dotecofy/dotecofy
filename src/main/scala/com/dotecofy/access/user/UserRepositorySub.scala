
package com.dotecofy.access.user

sealed trait UserRepositoryComponentSub extends UserRepositoryComponent

object UserRepositorySub extends UserRepository with UserRepositoryComponentSub {

}