
package com.dotecofy.improvement.improvement

import java.sql.Connection
import java.time.ZonedDateTime

import cloud.dest.sbf.auth.client.Authenticate
import cloud.dest.sbf.auth.client.Authenticate.Profile
import cloud.dest.sbf.auth.client.profile.{ProfileRepositoryComponentSub, ProfileRepositorySub}
import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DataSource
import com.github.slugify.Slugify

import scala.collection.mutable.ArrayBuffer

import com.dotecofy.workspace.feature._


case class ImprovementSrv(
     featureName: String = null,
     featureSig: String = null,

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait ImprovementServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_IMPROVEMENT_FORM = "invalid_improvement_form"
    val UPDATE_IMPROVEMENT_NOT_ALLOWED = "update_improvement_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pImprovement: ImprovementSrv)(implicit repository: ImprovementRepositoryComponentSub, repFeature:FeatureRepositoryComponentSub): Either[Error, ImprovementSrv]

    def update(token: String, signature: String, pImprovement: ImprovementSrv)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, ImprovementSelect]

    def delete(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, Option[ImprovementSelect]]
            def findByFeature(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]]
                            
    def findByCycle(token:String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]]
                    
    def findByLayer(token:String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]]
                    
    def findByVersion(token:String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]]
                    
    def findByKind(token:String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]]
            
}

abstract class ImprovementServices extends ImprovementServicesComponent {

    override def create(token : String,pImprovement: ImprovementSrv)(implicit repository: ImprovementRepositoryComponentSub, repFeature:FeatureRepositoryComponentSub): Either[Error, ImprovementSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovement.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovement.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovement.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovement.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovement.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENT_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val feature: FeatureDB = repFeature.findIfAllowed(profile.id, pImprovement.featureSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("featureSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("featureSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val improvementDB = ImprovementDB(

                    
                idFeature = feature.id,
                            
            
                signature = new Slugify().slugify(pImprovement.name),
                                        
                 name = pImprovement.name ,
                                
                 description = Option(pImprovement.description) ,
                            )
        
        repository.create(improvementDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pImprovement: ImprovementSrv)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, ImprovementSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovement.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovement.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovement.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovement.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovement.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENT_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementDB:ImprovementDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENT_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENT_NOT_ALLOWED, "Signature="+signature))
            }
        }

                        
        val updatedImprovement = ImprovementDB(
                                    
                signature = new Slugify().slugify(pImprovement.name),
                            
                 name = pImprovement.name ,
                                
                 description = Option(pImprovement.description) ,
                            )

        repository.update(improvementDB.id, updatedImprovement) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementDB:ImprovementDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENT_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENT_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(improvementDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(improvements) => Right(improvements.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, Option[ImprovementSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByFeature(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByFeature(signature) match {
            case Left(error) => Left(error)
            case Right(improvements) => Right(improvements.map(ws => dbToSrv(ws)))
        }
    }

        
        def findByCycle(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]] = {

            val profile: Profile = Authenticate.findOrCreateProfile(token) match {
                case Left(e) => return Left(e)
            }

            repository.findByCycle(signature) match {
                case Left(error) => Left(error)
                case Right(improvements) => Right(improvements.map(ws => dbToSrv(ws)))
            }
        }
        
        def findByLayer(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]] = {

            val profile: Profile = Authenticate.findOrCreateProfile(token) match {
                case Left(e) => return Left(e)
            }

            repository.findByLayer(signature) match {
                case Left(error) => Left(error)
                case Right(improvements) => Right(improvements.map(ws => dbToSrv(ws)))
            }
        }
        
        def findByVersion(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]] = {

            val profile: Profile = Authenticate.findOrCreateProfile(token) match {
                case Left(e) => return Left(e)
            }

            repository.findByVersion(signature) match {
                case Left(error) => Left(error)
                case Right(improvements) => Right(improvements.map(ws => dbToSrv(ws)))
            }
        }
        
        def findByKind(token: String, signature: String)(implicit repository: ImprovementRepositoryComponentSub): Either[Error, List[ImprovementSrv]] = {

            val profile: Profile = Authenticate.findOrCreateProfile(token) match {
                case Left(e) => return Left(e)
            }

            repository.findByKind(signature) match {
                case Left(error) => Left(error)
                case Right(improvements) => Right(improvements.map(ws => dbToSrv(ws)))
            }
        }
    
private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pImprovement: ImprovementSelect): ImprovementSrv = {
ImprovementSrv(
     featureName =  pImprovement.featureName ,
     featureSig =  pImprovement.featureSig ,

    signature = pImprovement.signature,
     name=pImprovement.name,
         description=pImprovement.description.getOrElse(null),
    
    createdDate = pImprovement.createdDate,

    updatedDate = pImprovement.updatedDate.orNull,
)
}
}
