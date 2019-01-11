
package com.dotecofy.improvement.improvementlayer

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
import com.dotecofy.context.layer._


    import scala.language.postfixOps
    
case class ImprovementLayerSrv(
     improvementName: String = null,
     improvementSig: String = null,
     layerName: String = null,
     layerSig: String = null,

    signature:String = null,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait ImprovementLayerServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_IMPROVEMENTLAYER_FORM = "invalid_improvementlayer_form"
    val UPDATE_IMPROVEMENTLAYER_NOT_ALLOWED = "update_improvementlayer_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pImprovementLayer: ImprovementLayerSrv)(implicit repository: ImprovementLayerRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repLayer:LayerRepositoryComponentSub): Either[Error, ImprovementLayerSrv]

    def update(token: String, signature: String, pImprovementLayer: ImprovementLayerSrv)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, ImprovementLayerSelect]

    def delete(token: String, signature: String)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, List[ImprovementLayerSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, Option[ImprovementLayerSelect]]
            def findByImprovement(token: String, signature: String)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, List[ImprovementLayerSrv]]
        def findByLayer(token: String, signature: String)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, List[ImprovementLayerSrv]]
        
}

abstract class ImprovementLayerServices extends ImprovementLayerServicesComponent {

    override def create(token : String,pImprovementLayer: ImprovementLayerSrv)(implicit repository: ImprovementLayerRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repLayer:LayerRepositoryComponentSub): Either[Error, ImprovementLayerSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovementLayer.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovementLayer.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovementLayer.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovementLayer.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovementLayer.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTLAYER_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val improvement: ImprovementDB = repImprovement.findIfAllowed(profile.id, pImprovementLayer.improvementSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTLAYER_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTLAYER_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            }
        }
                        
        val layer: LayerDB = repLayer.findIfAllowed(profile.id, pImprovementLayer.layerSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTLAYER_FORM, "Please verify the form", List(ErrorBuilder.fieldError("layerSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTLAYER_FORM, "Please verify the form", List(ErrorBuilder.fieldError("layerSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val improvementlayerDB = ImprovementLayerDB(

                    
                idImprovement = improvement.id,
                                
                idLayer = layer.id,
                            
            
                signature = (scala.util.Random.alphanumeric take 12).mkString,
                                    )
        
        repository.create(improvementlayerDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pImprovementLayer: ImprovementLayerSrv)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, ImprovementLayerSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovementLayer.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovementLayer.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovementLayer.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovementLayer.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovementLayer.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTLAYER_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementlayerDB:ImprovementLayerDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENTLAYER_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENTLAYER_NOT_ALLOWED, "Signature="+signature))
            }
        }

                                        
        val updatedImprovementLayer = ImprovementLayerDB(
                                                                        )

        repository.update(improvementlayerDB.id, updatedImprovementLayer) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementlayerDB:ImprovementLayerDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENTLAYER_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENTLAYER_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(improvementlayerDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, List[ImprovementLayerSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(improvementlayers) => Right(improvementlayers.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, Option[ImprovementLayerSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByImprovement(token: String, signature: String)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, List[ImprovementLayerSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByImprovement(signature) match {
            case Left(error) => Left(error)
            case Right(improvementlayers) => Right(improvementlayers.map(ws => dbToSrv(ws)))
        }
    }
    def findByLayer(token: String, signature: String)(implicit repository: ImprovementLayerRepositoryComponentSub): Either[Error, List[ImprovementLayerSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByLayer(signature) match {
            case Left(error) => Left(error)
            case Right(improvementlayers) => Right(improvementlayers.map(ws => dbToSrv(ws)))
        }
    }


private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTLAYER_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pImprovementLayer: ImprovementLayerSelect): ImprovementLayerSrv = {
ImprovementLayerSrv(
     improvementName =  pImprovementLayer.improvementName ,
     improvementSig =  pImprovementLayer.improvementSig ,
     layerName =  pImprovementLayer.layerName ,
     layerSig =  pImprovementLayer.layerSig ,

    signature = pImprovementLayer.signature,

    createdDate = pImprovementLayer.createdDate,

    updatedDate = pImprovementLayer.updatedDate.orNull,
)
}
}
