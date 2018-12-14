package com.dotecofy.workspace.project

trait ProjectServicesComponent {

  def create(userId:Int, workspaceSig:String )

}

object ProjectServices extends ProjectServicesComponent {
  override def create(userId: Int, workspaceSig: String): Unit = ???
}
