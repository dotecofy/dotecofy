
package com.dotecofy.workspace.version

import java.sql.Connection
import java.time.ZonedDateTime

import cloud.dest.sbf.auth.client.Authenticate
import cloud.dest.sbf.auth.client.Authenticate.Profile
import cloud.dest.sbf.auth.client.profile.{ProfileRepositoryComponentSub, ProfileRepositorySub}
import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DataSource
import com.github.slugify.Slugify

import scala.collection.mutable.ArrayBuffer

import com.dotecofy.workspace.project._


case class VersionSrv(
     projectName: String = null,
     projectSig: String = null,

    signature:String = null,
    version: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait VersionServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_VERSION_FORM = "invalid_version_form"
    val UPDATE_VERSION_NOT_ALLOWED = "update_version_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pVersion: VersionSrv)(implicit repository: VersionRepositoryComponentSub, repProject:ProjectRepositoryComponentSub): Either[Error, VersionSrv]

    def update(token: String, signature: String, pVersion: VersionSrv)(implicit repository: VersionRepositoryComponentSub): Either[Error, VersionSelect]

    def delete(token: String, signature: String)(implicit repository: VersionRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: VersionRepositoryComponentSub): Either[Error, List[VersionSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: VersionRepositoryComponentSub): Either[Error, Option[VersionSelect]]
            def findByProject(token: String, signature: String)(implicit repository: VersionRepositoryComponentSub): Either[Error, List[VersionSrv]]
                
    def findByImprovement(token:String, signature: String)(implicit repository: VersionRepositoryComponentSub): Either[Error, List[VersionSrv]]
            
}

abstract class VersionServices extends VersionServicesComponent {

    override def create(token : String,pVersion: VersionSrv)(implicit repository: VersionRepositoryComponentSub, repProject:ProjectRepositoryComponentSub): Either[Error, VersionSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pVersion.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pVersion.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pVersion.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pVersion.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pVersion.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_VERSION_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val project: ProjectDB = repProject.findIfAllowed(profile.id, pVersion.projectSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_VERSION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("projectSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_VERSION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("projectSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val versionDB = VersionDB(

                    
                idProject = project.id,
                            
            
                signature = new Slugify().slugify(pVersion.version),
                                        
                 version = pVersion.version ,
                                
                 description = Option(pVersion.description) ,
                            )
        
        repository.create(versionDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pVersion: VersionSrv)(implicit repository: VersionRepositoryComponentSub): Either[Error, VersionSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pVersion.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pVersion.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pVersion.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pVersion.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pVersion.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_VERSION_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val versionDB:VersionDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_VERSION_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_VERSION_NOT_ALLOWED, "Signature="+signature))
            }
        }

                        
        val updatedVersion = VersionDB(
                                    
                signature = new Slugify().slugify(pVersion.version),
                            
                 version = pVersion.version ,
                                
                 description = Option(pVersion.description) ,
                            )

        repository.update(versionDB.id, updatedVersion) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: VersionRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val versionDB:VersionDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_VERSION_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_VERSION_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(versionDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: VersionRepositoryComponentSub): Either[Error, List[VersionSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(versions) => Right(versions.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: VersionRepositoryComponentSub): Either[Error, Option[VersionSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByProject(token: String, signature: String)(implicit repository: VersionRepositoryComponentSub): Either[Error, List[VersionSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByProject(signature) match {
            case Left(error) => Left(error)
            case Right(versions) => Right(versions.map(ws => dbToSrv(ws)))
        }
    }

    
        def findByImprovement(token: String, signature: String)(implicit repository: VersionRepositoryComponentSub): Either[Error, List[VersionSrv]] = {

            val profile: Profile = Authenticate.findOrCreateProfile(token) match {
                case Left(e) => return Left(e)
            }

            repository.findByImprovement(signature) match {
                case Left(error) => Left(error)
                case Right(versions) => Right(versions.map(ws => dbToSrv(ws)))
            }
        }
    
private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_VERSION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pVersion: VersionSelect): VersionSrv = {
VersionSrv(
     projectName =  pVersion.projectName ,
     projectSig =  pVersion.projectSig ,

    signature = pVersion.signature,
     version=pVersion.version,
         description=pVersion.description.getOrElse(null),
    
    createdDate = pVersion.createdDate,

    updatedDate = pVersion.updatedDate.orNull,
)
}
}
