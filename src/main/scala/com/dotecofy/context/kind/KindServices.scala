
package com.dotecofy.context.kind

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


case class KindSrv(
     workspaceName: String = null,
     workspaceSig: String = null,

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait KindServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_KIND_FORM = "invalid_kind_form"
    val UPDATE_KIND_NOT_ALLOWED = "update_kind_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pKind: KindSrv)(implicit repository: KindRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, KindSrv]

    def update(token: String, signature: String, pKind: KindSrv)(implicit repository: KindRepositoryComponentSub): Either[Error, KindSelect]

    def delete(token: String, signature: String)(implicit repository: KindRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: KindRepositoryComponentSub): Either[Error, List[KindSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: KindRepositoryComponentSub): Either[Error, Option[KindSelect]]
            def findByWorkspace(token: String, signature: String)(implicit repository: KindRepositoryComponentSub): Either[Error, List[KindSrv]]
                
    def findByImprovement(token:String, signature: String)(implicit repository: KindRepositoryComponentSub): Either[Error, List[KindSrv]]
            
}

abstract class KindServices extends KindServicesComponent {

    override def create(token : String,pKind: KindSrv)(implicit repository: KindRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, KindSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pKind.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pKind.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pKind.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pKind.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pKind.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_KIND_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val workspace: WorkspaceDB = repWorkspace.findIfAllowed(profile.id, pKind.workspaceSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_KIND_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_KIND_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val kindDB = KindDB(

                    
                idWorkspace = workspace.id,
                            
            
                signature = new Slugify().slugify(pKind.name),
                                        
                 name = pKind.name ,
                                
                 description = Option(pKind.description) ,
                            )
        
        repository.create(kindDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pKind: KindSrv)(implicit repository: KindRepositoryComponentSub): Either[Error, KindSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pKind.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pKind.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pKind.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pKind.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pKind.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_KIND_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val kindDB:KindDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_KIND_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_KIND_NOT_ALLOWED, "Signature="+signature))
            }
        }

                        
        val updatedKind = KindDB(
                                    
                signature = new Slugify().slugify(pKind.name),
                            
                 name = pKind.name ,
                                
                 description = Option(pKind.description) ,
                            )

        repository.update(kindDB.id, updatedKind) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: KindRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val kindDB:KindDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_KIND_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_KIND_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(kindDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: KindRepositoryComponentSub): Either[Error, List[KindSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(kinds) => Right(kinds.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: KindRepositoryComponentSub): Either[Error, Option[KindSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByWorkspace(token: String, signature: String)(implicit repository: KindRepositoryComponentSub): Either[Error, List[KindSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByWorkspace(signature) match {
            case Left(error) => Left(error)
            case Right(kinds) => Right(kinds.map(ws => dbToSrv(ws)))
        }
    }

    
        def findByImprovement(token: String, signature: String)(implicit repository: KindRepositoryComponentSub): Either[Error, List[KindSrv]] = {

            val profile: Profile = Authenticate.findOrCreateProfile(token) match {
                case Left(e) => return Left(e)
            }

            repository.findByImprovement(signature) match {
                case Left(error) => Left(error)
                case Right(kinds) => Right(kinds.map(ws => dbToSrv(ws)))
            }
        }
    
private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_KIND_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pKind: KindSelect): KindSrv = {
KindSrv(
     workspaceName =  pKind.workspaceName ,
     workspaceSig =  pKind.workspaceSig ,

    signature = pKind.signature,
     name=pKind.name,
         description=pKind.description.getOrElse(null),
    
    createdDate = pKind.createdDate,

    updatedDate = pKind.updatedDate.orNull,
)
}
}
