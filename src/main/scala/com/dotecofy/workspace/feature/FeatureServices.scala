
package com.dotecofy.workspace.feature

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


case class FeatureSrv(
     projectName: String = null,
     projectSig: String = null,

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait FeatureServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_FEATURE_FORM = "invalid_feature_form"
    val UPDATE_FEATURE_NOT_ALLOWED = "update_feature_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pFeature: FeatureSrv)(implicit repository: FeatureRepositoryComponentSub, repProject:ProjectRepositoryComponentSub): Either[Error, FeatureSrv]

    def update(token: String, signature: String, pFeature: FeatureSrv)(implicit repository: FeatureRepositoryComponentSub): Either[Error, FeatureSelect]

    def delete(token: String, signature: String)(implicit repository: FeatureRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: FeatureRepositoryComponentSub): Either[Error, List[FeatureSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: FeatureRepositoryComponentSub): Either[Error, Option[FeatureSelect]]
            def findByProject(token: String, signature: String)(implicit repository: FeatureRepositoryComponentSub): Either[Error, List[FeatureSrv]]
        
}

abstract class FeatureServices extends FeatureServicesComponent {

    override def create(token : String,pFeature: FeatureSrv)(implicit repository: FeatureRepositoryComponentSub, repProject:ProjectRepositoryComponentSub): Either[Error, FeatureSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pFeature.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pFeature.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pFeature.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pFeature.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pFeature.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_FEATURE_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val project: ProjectDB = repProject.findIfAllowed(profile.id, pFeature.projectSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_FEATURE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("projectSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_FEATURE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("projectSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val featureDB = FeatureDB(

                    
                idProject = project.id,
                            
            
                signature = new Slugify().slugify(pFeature.name),
                                        
                 name = pFeature.name ,
                                
                 description = Option(pFeature.description) ,
                            )
        
        repository.create(featureDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pFeature: FeatureSrv)(implicit repository: FeatureRepositoryComponentSub): Either[Error, FeatureSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pFeature.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pFeature.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pFeature.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pFeature.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pFeature.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_FEATURE_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val featureDB:FeatureDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_FEATURE_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_FEATURE_NOT_ALLOWED, "Signature="+signature))
            }
        }

                        
        val updatedFeature = FeatureDB(
                                    
                signature = new Slugify().slugify(pFeature.name),
                            
                 name = pFeature.name ,
                                
                 description = Option(pFeature.description) ,
                            )

        repository.update(featureDB.id, updatedFeature) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: FeatureRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val featureDB:FeatureDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_FEATURE_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_FEATURE_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(featureDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: FeatureRepositoryComponentSub): Either[Error, List[FeatureSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(features) => Right(features.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: FeatureRepositoryComponentSub): Either[Error, Option[FeatureSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByProject(token: String, signature: String)(implicit repository: FeatureRepositoryComponentSub): Either[Error, List[FeatureSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByProject(signature) match {
            case Left(error) => Left(error)
            case Right(features) => Right(features.map(ws => dbToSrv(ws)))
        }
    }


private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_FEATURE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pFeature: FeatureSelect): FeatureSrv = {
FeatureSrv(
     projectName =  pFeature.projectName ,
     projectSig =  pFeature.projectSig ,

    signature = pFeature.signature,
     name=pFeature.name,
         description=pFeature.description.getOrElse(null),
    
    createdDate = pFeature.createdDate,

    updatedDate = pFeature.updatedDate.orNull,
)
}
}
