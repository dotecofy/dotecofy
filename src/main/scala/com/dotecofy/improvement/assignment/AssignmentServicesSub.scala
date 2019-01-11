
package com.dotecofy.improvement.assignment

sealed trait AssignmentServicesComponentSub extends AssignmentServicesComponent {

  implicit val repository: AssignmentRepositoryComponentSub = AssignmentRepositorySub

}

object AssignmentServicesSub extends AssignmentServices with AssignmentServicesComponentSub {

}