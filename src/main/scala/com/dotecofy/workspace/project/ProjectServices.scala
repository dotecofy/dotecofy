
package com.dotecofy.workspace.project

import java.sql.Connection
import java.time.ZonedDateTime

import cloud.dest.sbf.auth.client.Authenticate
import cloud.dest.sbf.auth.client.Authenticate.Profile
import cloud.dest.sbf.auth.client.profile.{ProfileRepositoryComponentSub, ProfileRepositorySub}
import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DataSource
import com.github.slugify.Slugify

import scala.collection.mutable.ArrayBuffer

import com.dotecofy.workspace.workspace._


case class ProjectSrv(
     workspaceName: String = null,
     workspaceSig: String = null,

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait ProjectServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_PROJECT_FORM = "invalid_project_form"
    val UPDATE_PROJECT_NOT_ALLOWED = "update_project_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pProject: ProjectSrv)(implicit repository: ProjectRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, ProjectSrv]

    def update(token: String, signature: String, pProject: ProjectSrv)(implicit repository: ProjectRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, ProjectSelect]

    def delete(token: String, signature: String)(implicit repository: ProjectRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ProjectRepositoryComponentSub): Either[Error, List[ProjectSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: ProjectRepositoryComponentSub): Either[Error, Option[ProjectSelect]]
            def findByWorkspace(token: String, signature: String)(implicit repository: ProjectRepositoryComponentSub): Either[Error, List[ProjectSrv]]
                                
}

abstract class ProjectServices extends ProjectServicesComponent {

    override def create(token : String,pProject: ProjectSrv)(implicit repository: ProjectRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, ProjectSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pProject.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pProject.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pProject.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pProject.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pProject.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_PROJECT_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val workspace: WorkspaceDB = repWorkspace.findIfAllowed(profile.id, pProject.workspaceSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_PROJECT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_PROJECT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val projectDB = ProjectDB(

                    
                idWorkspace = workspace.id,
                            
            
                signature = new Slugify().slugify(pProject.name),
                                        
                 name = pProject.name ,
                                
                 description = Option(pProject.description) ,
                            )
        
        repository.create(projectDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pProject: ProjectSrv)(implicit repository: ProjectRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, ProjectSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pProject.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pProject.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pProject.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pProject.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pProject.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_PROJECT_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val projectDB:ProjectDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_PROJECT_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_PROJECT_NOT_ALLOWED, "Signature="+signature))
            }
        }

                
        val workspace: WorkspaceDB = repWorkspace.findIfAllowed(profile.id, pProject.workspaceSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_PROJECT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_PROJECT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val updatedProject = ProjectDB(
                    
                idWorkspace = workspace.id,
                            
                signature = new Slugify().slugify(pProject.name),
                            
                 name = pProject.name ,
                                
                 description = Option(pProject.description) ,
                            )

        repository.update(projectDB.id, updatedProject) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: ProjectRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val projectDB:ProjectDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_PROJECT_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_PROJECT_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(projectDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ProjectRepositoryComponentSub): Either[Error, List[ProjectSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(projects) => Right(projects.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: ProjectRepositoryComponentSub): Either[Error, Option[ProjectSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByWorkspace(token: String, signature: String)(implicit repository: ProjectRepositoryComponentSub): Either[Error, List[ProjectSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByWorkspace(signature) match {
            case Left(error) => Left(error)
            case Right(projects) => Right(projects.map(ws => dbToSrv(ws)))
        }
    }

        
private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_PROJECT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pProject: ProjectSelect): ProjectSrv = {
ProjectSrv(
     workspaceName =  pProject.workspaceName ,
     workspaceSig =  pProject.workspaceSig ,

    signature = pProject.signature,
     name=pProject.name,
         description=pProject.description.getOrElse(null),
    
    createdDate = pProject.createdDate,

    updatedDate = pProject.updatedDate.orNull,
)
}
}
