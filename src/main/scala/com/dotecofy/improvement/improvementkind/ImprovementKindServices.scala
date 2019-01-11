
package com.dotecofy.improvement.improvementkind

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
import com.dotecofy.context.kind._


    import scala.language.postfixOps
    
case class ImprovementKindSrv(
     improvementName: String = null,
     improvementSig: String = null,
     kindName: String = null,
     kindSig: String = null,

    signature:String = null,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait ImprovementKindServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_IMPROVEMENTKIND_FORM = "invalid_improvementkind_form"
    val UPDATE_IMPROVEMENTKIND_NOT_ALLOWED = "update_improvementkind_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pImprovementKind: ImprovementKindSrv)(implicit repository: ImprovementKindRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repKind:KindRepositoryComponentSub): Either[Error, ImprovementKindSrv]

    def update(token: String, signature: String, pImprovementKind: ImprovementKindSrv)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, ImprovementKindSelect]

    def delete(token: String, signature: String)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, List[ImprovementKindSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, Option[ImprovementKindSelect]]
            def findByImprovement(token: String, signature: String)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, List[ImprovementKindSrv]]
        def findByKind(token: String, signature: String)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, List[ImprovementKindSrv]]
        
}

abstract class ImprovementKindServices extends ImprovementKindServicesComponent {

    override def create(token : String,pImprovementKind: ImprovementKindSrv)(implicit repository: ImprovementKindRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repKind:KindRepositoryComponentSub): Either[Error, ImprovementKindSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovementKind.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovementKind.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovementKind.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovementKind.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovementKind.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTKIND_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val improvement: ImprovementDB = repImprovement.findIfAllowed(profile.id, pImprovementKind.improvementSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTKIND_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTKIND_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            }
        }
                        
        val kind: KindDB = repKind.findIfAllowed(profile.id, pImprovementKind.kindSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTKIND_FORM, "Please verify the form", List(ErrorBuilder.fieldError("kindSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTKIND_FORM, "Please verify the form", List(ErrorBuilder.fieldError("kindSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val improvementkindDB = ImprovementKindDB(

                    
                idImprovement = improvement.id,
                                
                idKind = kind.id,
                            
            
                signature = (scala.util.Random.alphanumeric take 12).mkString,
                                    )
        
        repository.create(improvementkindDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pImprovementKind: ImprovementKindSrv)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, ImprovementKindSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovementKind.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovementKind.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovementKind.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovementKind.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovementKind.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTKIND_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementkindDB:ImprovementKindDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENTKIND_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENTKIND_NOT_ALLOWED, "Signature="+signature))
            }
        }

                                        
        val updatedImprovementKind = ImprovementKindDB(
                                                                        )

        repository.update(improvementkindDB.id, updatedImprovementKind) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementkindDB:ImprovementKindDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENTKIND_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENTKIND_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(improvementkindDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, List[ImprovementKindSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(improvementkinds) => Right(improvementkinds.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, Option[ImprovementKindSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByImprovement(token: String, signature: String)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, List[ImprovementKindSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByImprovement(signature) match {
            case Left(error) => Left(error)
            case Right(improvementkinds) => Right(improvementkinds.map(ws => dbToSrv(ws)))
        }
    }
    def findByKind(token: String, signature: String)(implicit repository: ImprovementKindRepositoryComponentSub): Either[Error, List[ImprovementKindSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByKind(signature) match {
            case Left(error) => Left(error)
            case Right(improvementkinds) => Right(improvementkinds.map(ws => dbToSrv(ws)))
        }
    }


private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTKIND_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pImprovementKind: ImprovementKindSelect): ImprovementKindSrv = {
ImprovementKindSrv(
     improvementName =  pImprovementKind.improvementName ,
     improvementSig =  pImprovementKind.improvementSig ,
     kindName =  pImprovementKind.kindName ,
     kindSig =  pImprovementKind.kindSig ,

    signature = pImprovementKind.signature,

    createdDate = pImprovementKind.createdDate,

    updatedDate = pImprovementKind.updatedDate.orNull,
)
}
}
