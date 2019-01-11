
package com.dotecofy.context.kind

sealed trait KindServicesComponentSub extends KindServicesComponent {

  implicit val repository: KindRepositoryComponentSub = KindRepositorySub

}

object KindServicesSub extends KindServices with KindServicesComponentSub {

}