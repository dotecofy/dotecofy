
package com.dotecofy.improvement.improvementcycle

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
import com.dotecofy.context.cycle._


    import scala.language.postfixOps
    
case class ImprovementCycleSrv(
     improvementName: String = null,
     improvementSig: String = null,
     cycleName: String = null,
     cycleSig: String = null,

    signature:String = null,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait ImprovementCycleServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_IMPROVEMENTCYCLE_FORM = "invalid_improvementcycle_form"
    val UPDATE_IMPROVEMENTCYCLE_NOT_ALLOWED = "update_improvementcycle_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pImprovementCycle: ImprovementCycleSrv)(implicit repository: ImprovementCycleRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repCycle:CycleRepositoryComponentSub): Either[Error, ImprovementCycleSrv]

    def update(token: String, signature: String, pImprovementCycle: ImprovementCycleSrv)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, ImprovementCycleSelect]

    def delete(token: String, signature: String)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, List[ImprovementCycleSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, Option[ImprovementCycleSelect]]
            def findByImprovement(token: String, signature: String)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, List[ImprovementCycleSrv]]
        def findByCycle(token: String, signature: String)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, List[ImprovementCycleSrv]]
        
}

abstract class ImprovementCycleServices extends ImprovementCycleServicesComponent {

    override def create(token : String,pImprovementCycle: ImprovementCycleSrv)(implicit repository: ImprovementCycleRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repCycle:CycleRepositoryComponentSub): Either[Error, ImprovementCycleSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovementCycle.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovementCycle.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovementCycle.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovementCycle.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovementCycle.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTCYCLE_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val improvement: ImprovementDB = repImprovement.findIfAllowed(profile.id, pImprovementCycle.improvementSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTCYCLE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTCYCLE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            }
        }
                        
        val cycle: CycleDB = repCycle.findIfAllowed(profile.id, pImprovementCycle.cycleSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTCYCLE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("cycleSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTCYCLE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("cycleSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val improvementcycleDB = ImprovementCycleDB(

                    
                idImprovement = improvement.id,
                                
                idCycle = cycle.id,
                            
            
                signature = (scala.util.Random.alphanumeric take 12).mkString,
                                    )
        
        repository.create(improvementcycleDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pImprovementCycle: ImprovementCycleSrv)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, ImprovementCycleSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pImprovementCycle.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pImprovementCycle.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pImprovementCycle.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pImprovementCycle.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pImprovementCycle.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTCYCLE_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementcycleDB:ImprovementCycleDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENTCYCLE_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENTCYCLE_NOT_ALLOWED, "Signature="+signature))
            }
        }

                                        
        val updatedImprovementCycle = ImprovementCycleDB(
                                                                        )

        repository.update(improvementcycleDB.id, updatedImprovementCycle) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val improvementcycleDB:ImprovementCycleDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_IMPROVEMENTCYCLE_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_IMPROVEMENTCYCLE_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(improvementcycleDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, List[ImprovementCycleSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(improvementcycles) => Right(improvementcycles.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, Option[ImprovementCycleSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByImprovement(token: String, signature: String)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, List[ImprovementCycleSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByImprovement(signature) match {
            case Left(error) => Left(error)
            case Right(improvementcycles) => Right(improvementcycles.map(ws => dbToSrv(ws)))
        }
    }
    def findByCycle(token: String, signature: String)(implicit repository: ImprovementCycleRepositoryComponentSub): Either[Error, List[ImprovementCycleSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByCycle(signature) match {
            case Left(error) => Left(error)
            case Right(improvementcycles) => Right(improvementcycles.map(ws => dbToSrv(ws)))
        }
    }


private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_IMPROVEMENTCYCLE_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pImprovementCycle: ImprovementCycleSelect): ImprovementCycleSrv = {
ImprovementCycleSrv(
     improvementName =  pImprovementCycle.improvementName ,
     improvementSig =  pImprovementCycle.improvementSig ,
     cycleName =  pImprovementCycle.cycleName ,
     cycleSig =  pImprovementCycle.cycleSig ,

    signature = pImprovementCycle.signature,

    createdDate = pImprovementCycle.createdDate,

    updatedDate = pImprovementCycle.updatedDate.orNull,
)
}
}
