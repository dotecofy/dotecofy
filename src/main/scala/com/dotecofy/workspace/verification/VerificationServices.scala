
package com.dotecofy.workspace.verification

import java.sql.Connection
import java.time.ZonedDateTime

import cloud.dest.sbf.auth.client.Authenticate
import cloud.dest.sbf.auth.client.Authenticate.Profile
import cloud.dest.sbf.auth.client.profile.{ProfileRepositoryComponentSub, ProfileRepositorySub}
import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DataSource
import com.github.slugify.Slugify

import scala.collection.mutable.ArrayBuffer

import com.dotecofy.improvement.output._


    import scala.language.postfixOps
    
case class VerificationSrv(
     outputName: String = null,
     outputSig: String = null,

    signature:String = null,
    remark: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait VerificationServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_VERIFICATION_FORM = "invalid_verification_form"
    val UPDATE_VERIFICATION_NOT_ALLOWED = "update_verification_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pVerification: VerificationSrv)(implicit repository: VerificationRepositoryComponentSub, repOutput:OutputRepositoryComponentSub): Either[Error, VerificationSrv]

    def update(token: String, signature: String, pVerification: VerificationSrv)(implicit repository: VerificationRepositoryComponentSub): Either[Error, VerificationSelect]

    def delete(token: String, signature: String)(implicit repository: VerificationRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: VerificationRepositoryComponentSub): Either[Error, List[VerificationSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: VerificationRepositoryComponentSub): Either[Error, Option[VerificationSelect]]
            def findByOutput(token: String, signature: String)(implicit repository: VerificationRepositoryComponentSub): Either[Error, List[VerificationSrv]]
        
}

abstract class VerificationServices extends VerificationServicesComponent {

    override def create(token : String,pVerification: VerificationSrv)(implicit repository: VerificationRepositoryComponentSub, repOutput:OutputRepositoryComponentSub): Either[Error, VerificationSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pVerification.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pVerification.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pVerification.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pVerification.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pVerification.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_VERIFICATION_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val output: OutputDB = repOutput.findIfAllowed(profile.id, pVerification.outputSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_VERIFICATION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("outputSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_VERIFICATION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("outputSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val verificationDB = VerificationDB(

                    
                idOutput = output.id,
                            
            
                signature = (scala.util.Random.alphanumeric take 12).mkString,
                                        
                 remark = Option(pVerification.remark) ,
                            )
        
        repository.create(verificationDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pVerification: VerificationSrv)(implicit repository: VerificationRepositoryComponentSub): Either[Error, VerificationSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pVerification.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pVerification.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pVerification.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pVerification.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pVerification.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_VERIFICATION_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val verificationDB:VerificationDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_VERIFICATION_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_VERIFICATION_NOT_ALLOWED, "Signature="+signature))
            }
        }

                        
        val updatedVerification = VerificationDB(
                                                        
                 remark = Option(pVerification.remark) ,
                            )

        repository.update(verificationDB.id, updatedVerification) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: VerificationRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val verificationDB:VerificationDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_VERIFICATION_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_VERIFICATION_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(verificationDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: VerificationRepositoryComponentSub): Either[Error, List[VerificationSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(verifications) => Right(verifications.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: VerificationRepositoryComponentSub): Either[Error, Option[VerificationSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByOutput(token: String, signature: String)(implicit repository: VerificationRepositoryComponentSub): Either[Error, List[VerificationSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByOutput(signature) match {
            case Left(error) => Left(error)
            case Right(verifications) => Right(verifications.map(ws => dbToSrv(ws)))
        }
    }


private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_VERIFICATION_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pVerification: VerificationSelect): VerificationSrv = {
VerificationSrv(
     outputName =  pVerification.outputName ,
     outputSig =  pVerification.outputSig ,

    signature = pVerification.signature,
     remark=pVerification.remark.getOrElse(null),
    
    createdDate = pVerification.createdDate,

    updatedDate = pVerification.updatedDate.orNull,
)
}
}
