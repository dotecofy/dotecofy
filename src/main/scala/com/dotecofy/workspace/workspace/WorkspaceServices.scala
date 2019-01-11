
package com.dotecofy.workspace.workspace

import java.sql.Connection
import java.time.ZonedDateTime

import cloud.dest.sbf.auth.client.Authenticate
import cloud.dest.sbf.auth.client.Authenticate.Profile
import cloud.dest.sbf.auth.client.profile.{ProfileRepositoryComponentSub, ProfileRepositorySub}
import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DataSource
import com.github.slugify.Slugify

import scala.collection.mutable.ArrayBuffer



case class WorkspaceSrv(

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait WorkspaceServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_WORKSPACE_FORM = "invalid_workspace_form"
    val UPDATE_WORKSPACE_NOT_ALLOWED = "update_workspace_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pWorkspace: WorkspaceSrv)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, WorkspaceSrv]

    def update(token: String, signature: String, pWorkspace: WorkspaceSrv)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, WorkspaceSelect]

    def delete(token: String, signature: String)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, List[WorkspaceSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, Option[WorkspaceSelect]]
                                                            
}

abstract class WorkspaceServices extends WorkspaceServicesComponent {

    override def create(token : String,pWorkspace: WorkspaceSrv)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, WorkspaceSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pWorkspace.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pWorkspace.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pWorkspace.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pWorkspace.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pWorkspace.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_WORKSPACE_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        
        val workspaceDB = WorkspaceDB(

                
            
                signature = new Slugify().slugify(pWorkspace.name),
                                        
                 name = pWorkspace.name ,
                                
                 description = Option(pWorkspace.description) ,
                            )
        
        repository.create(workspaceDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pWorkspace: WorkspaceSrv)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, WorkspaceSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pWorkspace.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pWorkspace.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pWorkspace.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pWorkspace.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pWorkspace.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_WORKSPACE_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val workspaceDB:WorkspaceDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_WORKSPACE_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_WORKSPACE_NOT_ALLOWED, "Signature="+signature))
            }
        }

        
        val updatedWorkspace = WorkspaceDB(
                
                signature = new Slugify().slugify(pWorkspace.name),
                            
                 name = pWorkspace.name ,
                                
                 description = Option(pWorkspace.description) ,
                            )

        repository.update(workspaceDB.id, updatedWorkspace) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val workspaceDB:WorkspaceDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_WORKSPACE_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_WORKSPACE_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(workspaceDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, List[WorkspaceSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(workspaces) => Right(workspaces.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: WorkspaceRepositoryComponentSub): Either[Error, Option[WorkspaceSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }


                
private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_WORKSPACE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pWorkspace: WorkspaceSelect): WorkspaceSrv = {
WorkspaceSrv(

    signature = pWorkspace.signature,
     name=pWorkspace.name,
         description=pWorkspace.description.getOrElse(null),
    
    createdDate = pWorkspace.createdDate,

    updatedDate = pWorkspace.updatedDate.orNull,
)
}
}
