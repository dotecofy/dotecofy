
package com.dotecofy.workspace.verification

sealed trait VerificationServicesComponentSub extends VerificationServicesComponent {

  implicit val repository: VerificationRepositoryComponentSub = VerificationRepositorySub

}

object VerificationServicesSub extends VerificationServices with VerificationServicesComponentSub {

}