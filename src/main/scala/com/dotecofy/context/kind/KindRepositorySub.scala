
package com.dotecofy.context.kind

sealed trait KindRepositoryComponentSub extends KindRepositoryComponent

object KindRepositorySub extends KindRepository with KindRepositoryComponentSub {

}