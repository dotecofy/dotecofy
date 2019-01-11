
package com.dotecofy.improvement.output

import java.sql.Connection
import java.time.ZonedDateTime

import cloud.dest.sbf.auth.client.Authenticate
import cloud.dest.sbf.auth.client.Authenticate.Profile
import cloud.dest.sbf.auth.client.profile.{ProfileRepositoryComponentSub, ProfileRepositorySub}
import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DataSource
import com.github.slugify.Slugify

import scala.collection.mutable.ArrayBuffer

import com.dotecofy.improvement.assignment._
import com.dotecofy.improvement.improvementcycle._


    import scala.language.postfixOps
    
case class OutputSrv(
     assignmentName: String = null,
     assignmentSig: String = null,
     improvementcycleName: String = null,
     improvementcycleSig: String = null,

    signature:String = null,
    remark: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait OutputServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_OUTPUT_FORM = "invalid_output_form"
    val UPDATE_OUTPUT_NOT_ALLOWED = "update_output_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pOutput: OutputSrv)(implicit repository: OutputRepositoryComponentSub, repAssignment:AssignmentRepositoryComponentSub, repImprovementCycle:ImprovementCycleRepositoryComponentSub): Either[Error, OutputSrv]

    def update(token: String, signature: String, pOutput: OutputSrv)(implicit repository: OutputRepositoryComponentSub): Either[Error, OutputSelect]

    def delete(token: String, signature: String)(implicit repository: OutputRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: OutputRepositoryComponentSub): Either[Error, List[OutputSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: OutputRepositoryComponentSub): Either[Error, Option[OutputSelect]]
            def findByAssignment(token: String, signature: String)(implicit repository: OutputRepositoryComponentSub): Either[Error, List[OutputSrv]]
        def findByImprovementCycle(token: String, signature: String)(implicit repository: OutputRepositoryComponentSub): Either[Error, List[OutputSrv]]
        
}

abstract class OutputServices extends OutputServicesComponent {

    override def create(token : String,pOutput: OutputSrv)(implicit repository: OutputRepositoryComponentSub, repAssignment:AssignmentRepositoryComponentSub, repImprovementCycle:ImprovementCycleRepositoryComponentSub): Either[Error, OutputSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pOutput.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pOutput.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pOutput.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pOutput.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pOutput.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_OUTPUT_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val assignment: AssignmentDB = repAssignment.findIfAllowed(profile.id, pOutput.assignmentSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_OUTPUT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("assignmentSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_OUTPUT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("assignmentSig", CommonError.NOT_ALLOWED))))
            }
        }
                        
        val improvementcycle: ImprovementCycleDB = repImprovementCycle.findIfAllowed(profile.id, pOutput.improvementcycleSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_OUTPUT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementcycleSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_OUTPUT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementcycleSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val outputDB = OutputDB(

                    
                idAssignment = assignment.id,
                                
                idImprCycle = improvementcycle.id,
                            
            
                signature = (scala.util.Random.alphanumeric take 12).mkString,
                                        
                 remark = Option(pOutput.remark) ,
                            )
        
        repository.create(outputDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pOutput: OutputSrv)(implicit repository: OutputRepositoryComponentSub): Either[Error, OutputSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pOutput.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pOutput.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pOutput.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pOutput.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pOutput.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_OUTPUT_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val outputDB:OutputDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_OUTPUT_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_OUTPUT_NOT_ALLOWED, "Signature="+signature))
            }
        }

                                        
        val updatedOutput = OutputDB(
                                                                            
                 remark = Option(pOutput.remark) ,
                            )

        repository.update(outputDB.id, updatedOutput) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: OutputRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val outputDB:OutputDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_OUTPUT_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_OUTPUT_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(outputDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: OutputRepositoryComponentSub): Either[Error, List[OutputSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(outputs) => Right(outputs.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: OutputRepositoryComponentSub): Either[Error, Option[OutputSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByAssignment(token: String, signature: String)(implicit repository: OutputRepositoryComponentSub): Either[Error, List[OutputSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByAssignment(signature) match {
            case Left(error) => Left(error)
            case Right(outputs) => Right(outputs.map(ws => dbToSrv(ws)))
        }
    }
    def findByImprovementCycle(token: String, signature: String)(implicit repository: OutputRepositoryComponentSub): Either[Error, List[OutputSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByImprovementCycle(signature) match {
            case Left(error) => Left(error)
            case Right(outputs) => Right(outputs.map(ws => dbToSrv(ws)))
        }
    }


private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_OUTPUT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pOutput: OutputSelect): OutputSrv = {
OutputSrv(
     assignmentName =  pOutput.assignmentName ,
     assignmentSig =  pOutput.assignmentSig ,
     improvementcycleName =  pOutput.improvementcycleName ,
     improvementcycleSig =  pOutput.improvementcycleSig ,

    signature = pOutput.signature,
     remark=pOutput.remark.getOrElse(null),
    
    createdDate = pOutput.createdDate,

    updatedDate = pOutput.updatedDate.orNull,
)
}
}
