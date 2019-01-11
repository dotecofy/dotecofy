
package com.dotecofy.improvement.improvementversion

import java.sql.Connection
import java.time.ZonedDateTime

import cloud.dest.sbf.auth.client.Authenticate
import cloud.dest.sbf.auth.client.Authenticate.Profile
import cloud.dest.sbf.auth.client.profile.{ProfileRepositoryComponentSub, ProfileRepositorySub}
import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DataSource
import com.github.slugify.Slugify

import scala.collection.mutable.ArrayBuffer

import com.dotecofy.improvement.improvement._
import com.dotecofy.workspace.version._


    import scala.language.postfixOps
    
case class ImprovementVersionSrv(
     improvementName: String = null,
     improvementSig: String = null,
     versionName: String = null,
     versionSig: String = null,

    signature:String = null,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait ImprovementVersionServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_IMPROVEMENTVERSION_FORM = "invalid_improvementversion_form"
    val UPDATE_IMPROVEMENTVERSION_NOT_ALLOWED = "update_improvementversion_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pImprovementVersion: ImprovementVersionSrv)(implicit repository: ImprovementVersionRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repVersion:VersionRepositoryComponentSub): Either[Error, ImprovementVersionSrv]

    def update(token: String, signature: String, pImprovementVersion: ImprovementVersionSrv)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, ImprovementVersionSelect]

    def delete(token: String, signature: String)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, List[ImprovementVersionSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, Option[ImprovementVersionSelect]]
            def findByImprovement(token: String, signature: String)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, List[ImprovementVersionSrv]]
        def findByVersion(token: String, signature: String)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, List[ImprovementVersionSrv]]
        
}

abstract class ImprovementVersionServices extends ImprovementVersionServicesComponent {

    override def create(token : String,pImprovementVersion: ImprovementVersionSrv)(implicit repository: ImprovementVersionRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repVersion:VersionRepositoryComponentSub): Either[Error, ImprovementVersionSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovementVersion.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovementVersion.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovementVersion.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovementVersion.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovementVersion.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTVERSION_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val improvement: ImprovementDB = repImprovement.findIfAllowed(profile.id, pImprovementVersion.improvementSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTVERSION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTVERSION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            }
        }
                        
        val version: VersionDB = repVersion.findIfAllowed(profile.id, pImprovementVersion.versionSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTVERSION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("versionSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTVERSION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("versionSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val improvementversionDB = ImprovementVersionDB(

                    
                idImprovement = improvement.id,
                                
                idVersion = version.id,
                            
            
                signature = (scala.util.Random.alphanumeric take 12).mkString,
                                    )
        
        repository.create(improvementversionDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pImprovementVersion: ImprovementVersionSrv)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, ImprovementVersionSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovementVersion.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovementVersion.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovementVersion.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovementVersion.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovementVersion.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTVERSION_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementversionDB:ImprovementVersionDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENTVERSION_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENTVERSION_NOT_ALLOWED, "Signature="+signature))
            }
        }

                                        
        val updatedImprovementVersion = ImprovementVersionDB(
                                                                        )

        repository.update(improvementversionDB.id, updatedImprovementVersion) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementversionDB:ImprovementVersionDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENTVERSION_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENTVERSION_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(improvementversionDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, List[ImprovementVersionSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(improvementversions) => Right(improvementversions.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, Option[ImprovementVersionSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByImprovement(token: String, signature: String)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, List[ImprovementVersionSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByImprovement(signature) match {
            case Left(error) => Left(error)
            case Right(improvementversions) => Right(improvementversions.map(ws => dbToSrv(ws)))
        }
    }
    def findByVersion(token: String, signature: String)(implicit repository: ImprovementVersionRepositoryComponentSub): Either[Error, List[ImprovementVersionSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByVersion(signature) match {
            case Left(error) => Left(error)
            case Right(improvementversions) => Right(improvementversions.map(ws => dbToSrv(ws)))
        }
    }


private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTVERSION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pImprovementVersion: ImprovementVersionSelect): ImprovementVersionSrv = {
ImprovementVersionSrv(
     improvementName =  pImprovementVersion.improvementName ,
     improvementSig =  pImprovementVersion.improvementSig ,
     versionName =  pImprovementVersion.versionName ,
     versionSig =  pImprovementVersion.versionSig ,

    signature = pImprovementVersion.signature,

    createdDate = pImprovementVersion.createdDate,

    updatedDate = pImprovementVersion.updatedDate.orNull,
)
}
}
