
package com.dotecofy.access.group

import java.sql.Connection
import java.time.ZonedDateTime

import cloud.dest.sbf.auth.client.Authenticate
import cloud.dest.sbf.auth.client.Authenticate.Profile
import cloud.dest.sbf.auth.client.profile.{ProfileRepositoryComponentSub, ProfileRepositorySub}
import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DataSource
import com.github.slugify.Slugify

import scala.collection.mutable.ArrayBuffer



case class GroupSrv(

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait GroupServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_GROUP_FORM = "invalid_group_form"
    val UPDATE_GROUP_NOT_ALLOWED = "update_group_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pGroup: GroupSrv)(implicit repository: GroupRepositoryComponentSub): Either[Error, GroupSrv]

    def update(token: String, signature: String, pGroup: GroupSrv)(implicit repository: GroupRepositoryComponentSub): Either[Error, GroupSelect]

    def delete(token: String, signature: String)(implicit repository: GroupRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: GroupRepositoryComponentSub): Either[Error, List[GroupSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: GroupRepositoryComponentSub): Either[Error, Option[GroupSelect]]
                        
}

abstract class GroupServices extends GroupServicesComponent {

    override def create(token : String,pGroup: GroupSrv)(implicit repository: GroupRepositoryComponentSub): Either[Error, GroupSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pGroup.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pGroup.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pGroup.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pGroup.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pGroup.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_GROUP_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        
        val groupDB = GroupDB(

                
            
                signature = new Slugify().slugify(pGroup.name),
                                        
                 name = pGroup.name ,
                                
                 description = Option(pGroup.description) ,
                            )
        
        repository.create(groupDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pGroup: GroupSrv)(implicit repository: GroupRepositoryComponentSub): Either[Error, GroupSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pGroup.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pGroup.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pGroup.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pGroup.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pGroup.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_GROUP_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val groupDB:GroupDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_GROUP_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_GROUP_NOT_ALLOWED, "Signature="+signature))
            }
        }

        
        val updatedGroup = GroupDB(
                
                signature = new Slugify().slugify(pGroup.name),
                            
                 name = pGroup.name ,
                                
                 description = Option(pGroup.description) ,
                            )

        repository.update(groupDB.id, updatedGroup) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: GroupRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val groupDB:GroupDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_GROUP_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_GROUP_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(groupDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: GroupRepositoryComponentSub): Either[Error, List[GroupSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(groups) => Right(groups.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: GroupRepositoryComponentSub): Either[Error, Option[GroupSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }


    
private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_GROUP_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pGroup: GroupSelect): GroupSrv = {
GroupSrv(

    signature = pGroup.signature,
     name=pGroup.name,
         description=pGroup.description.getOrElse(null),
    
    createdDate = pGroup.createdDate,

    updatedDate = pGroup.updatedDate.orNull,
)
}
}
