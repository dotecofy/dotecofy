
package com.dotecofy.context.cycle

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


case class CycleSrv(
     workspaceName: String = null,
     workspaceSig: String = null,

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait CycleServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_CYCLE_FORM = "invalid_cycle_form"
    val UPDATE_CYCLE_NOT_ALLOWED = "update_cycle_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pCycle: CycleSrv)(implicit repository: CycleRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, CycleSrv]

    def update(token: String, signature: String, pCycle: CycleSrv)(implicit repository: CycleRepositoryComponentSub): Either[Error, CycleSelect]

    def delete(token: String, signature: String)(implicit repository: CycleRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: CycleRepositoryComponentSub): Either[Error, List[CycleSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: CycleRepositoryComponentSub): Either[Error, Option[CycleSelect]]
            def findByWorkspace(token: String, signature: String)(implicit repository: CycleRepositoryComponentSub): Either[Error, List[CycleSrv]]
                
    def findByImprovement(token:String, signature: String)(implicit repository: CycleRepositoryComponentSub): Either[Error, List[CycleSrv]]
            
}

abstract class CycleServices extends CycleServicesComponent {

    override def create(token : String,pCycle: CycleSrv)(implicit repository: CycleRepositoryComponentSub, repWorkspace:WorkspaceRepositoryComponentSub): Either[Error, CycleSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pCycle.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pCycle.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pCycle.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pCycle.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pCycle.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_CYCLE_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val workspace: WorkspaceDB = repWorkspace.findIfAllowed(profile.id, pCycle.workspaceSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_CYCLE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_CYCLE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("workspaceSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val cycleDB = CycleDB(

                    
                idWorkspace = workspace.id,
                            
            
                signature = new Slugify().slugify(pCycle.name),
                                        
                 name = pCycle.name ,
                                
                 description = Option(pCycle.description) ,
                            )
        
        repository.create(cycleDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pCycle: CycleSrv)(implicit repository: CycleRepositoryComponentSub): Either[Error, CycleSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pCycle.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pCycle.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pCycle.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pCycle.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pCycle.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_CYCLE_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val cycleDB:CycleDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_CYCLE_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_CYCLE_NOT_ALLOWED, "Signature="+signature))
            }
        }

                        
        val updatedCycle = CycleDB(
                                    
                signature = new Slugify().slugify(pCycle.name),
                            
                 name = pCycle.name ,
                                
                 description = Option(pCycle.description) ,
                            )

        repository.update(cycleDB.id, updatedCycle) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: CycleRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val cycleDB:CycleDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_CYCLE_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_CYCLE_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(cycleDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: CycleRepositoryComponentSub): Either[Error, List[CycleSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(cycles) => Right(cycles.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: CycleRepositoryComponentSub): Either[Error, Option[CycleSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByWorkspace(token: String, signature: String)(implicit repository: CycleRepositoryComponentSub): Either[Error, List[CycleSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByWorkspace(signature) match {
            case Left(error) => Left(error)
            case Right(cycles) => Right(cycles.map(ws => dbToSrv(ws)))
        }
    }

    
        def findByImprovement(token: String, signature: String)(implicit repository: CycleRepositoryComponentSub): Either[Error, List[CycleSrv]] = {

            val profile: Profile = Authenticate.findOrCreateProfile(token) match {
                case Left(e) => return Left(e)
            }

            repository.findByImprovement(signature) match {
                case Left(error) => Left(error)
                case Right(cycles) => Right(cycles.map(ws => dbToSrv(ws)))
            }
        }
    
private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_CYCLE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pCycle: CycleSelect): CycleSrv = {
CycleSrv(
     workspaceName =  pCycle.workspaceName ,
     workspaceSig =  pCycle.workspaceSig ,

    signature = pCycle.signature,
     name=pCycle.name,
         description=pCycle.description.getOrElse(null),
    
    createdDate = pCycle.createdDate,

    updatedDate = pCycle.updatedDate.orNull,
)
}
}
