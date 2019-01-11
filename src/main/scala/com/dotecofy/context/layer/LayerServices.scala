
package com.dotecofy.context.layer

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


case class LayerSrv(
     workspaceName: String = null,
     workspaceSig: String = null,

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait LayerServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_LAYER_FORM = "invalid_layer_form"
    val UPDATE_LAYER_NOT_ALLOWED = "update_layer_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pLayer: LayerSrv)(implicit repository: LayerRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, LayerSrv]

    def update(token: String, signature: String, pLayer: LayerSrv)(implicit repository: LayerRepositoryComponentSub): Either[Error, LayerSelect]

    def delete(token: String, signature: String)(implicit repository: LayerRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: LayerRepositoryComponentSub): Either[Error, List[LayerSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: LayerRepositoryComponentSub): Either[Error, Option[LayerSelect]]
            def findByWorkspace(token: String, signature: String)(implicit repository: LayerRepositoryComponentSub): Either[Error, List[LayerSrv]]
                
    def findByImprovement(token:String, signature: String)(implicit repository: LayerRepositoryComponentSub): Either[Error, List[LayerSrv]]
            
}

abstract class LayerServices extends LayerServicesComponent {

    override def create(token : String,pLayer: LayerSrv)(implicit repository: LayerRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, LayerSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pLayer.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pLayer.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pLayer.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pLayer.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pLayer.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_LAYER_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val workspace: WorkspaceDB = repWorkspace.findIfAllowed(profile.id, pLayer.workspaceSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_LAYER_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_LAYER_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val layerDB = LayerDB(

                    
                idWorkspace = workspace.id,
                            
            
                signature = new Slugify().slugify(pLayer.name),
                                        
                 name = pLayer.name ,
                                
                 description = Option(pLayer.description) ,
                            )
        
        repository.create(layerDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pLayer: LayerSrv)(implicit repository: LayerRepositoryComponentSub): Either[Error, LayerSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pLayer.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pLayer.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pLayer.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pLayer.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pLayer.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_LAYER_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val layerDB:LayerDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_LAYER_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_LAYER_NOT_ALLOWED, "Signature="+signature))
            }
        }

                        
        val updatedLayer = LayerDB(
                                    
                signature = new Slugify().slugify(pLayer.name),
                            
                 name = pLayer.name ,
                                
                 description = Option(pLayer.description) ,
                            )

        repository.update(layerDB.id, updatedLayer) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: LayerRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val layerDB:LayerDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_LAYER_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_LAYER_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(layerDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: LayerRepositoryComponentSub): Either[Error, List[LayerSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(layers) => Right(layers.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: LayerRepositoryComponentSub): Either[Error, Option[LayerSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByWorkspace(token: String, signature: String)(implicit repository: LayerRepositoryComponentSub): Either[Error, List[LayerSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByWorkspace(signature) match {
            case Left(error) => Left(error)
            case Right(layers) => Right(layers.map(ws => dbToSrv(ws)))
        }
    }

    
        def findByImprovement(token: String, signature: String)(implicit repository: LayerRepositoryComponentSub): Either[Error, List[LayerSrv]] = {

            val profile: Profile = Authenticate.findOrCreateProfile(token) match {
                case Left(e) => return Left(e)
            }

            repository.findByImprovement(signature) match {
                case Left(error) => Left(error)
                case Right(layers) => Right(layers.map(ws => dbToSrv(ws)))
            }
        }
    
private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_LAYER_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pLayer: LayerSelect): LayerSrv = {
LayerSrv(
     workspaceName =  pLayer.workspaceName ,
     workspaceSig =  pLayer.workspaceSig ,

    signature = pLayer.signature,
     name=pLayer.name,
         description=pLayer.description.getOrElse(null),
    
    createdDate = pLayer.createdDate,

    updatedDate = pLayer.updatedDate.orNull,
)
}
}
