
package com.dotecofy.improvement.assignment

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
import com.dotecofy.improvement.improvementkind._


case class AssignmentSrv(
     improvementName: String = null,
     improvementSig: String = null,
     improvementkindName: String = null,
     improvementkindSig: String = null,

    signature:String = null,
    name: String,
    description: String,

    createdDate: ZonedDateTime = null,

    updatedDate: ZonedDateTime = null,
)

trait AssignmentServicesComponent {

    implicit val profileRepository:ProfileRepositoryComponentSub = ProfileRepositorySub
    implicit def connection: Connection = DataSource.connection()

    val ERROR_INVALID_ASSIGNMENT_FORM = "invalid_assignment_form"
    val UPDATE_ASSIGNMENT_NOT_ALLOWED = "update_assignment_not_allowed"

    val SIGNATURE_MAX_LENGTH = 80
    val SIGNATURE_MIN_LENGTH = 3

    val NAME_MAX_LENGTH = 50
    val NAME_MIN_LENGTH = 3

    val DESCRIPTION_MAX_LENGTH = 200

    def create(token: String,pAssignment: AssignmentSrv)(implicit repository: AssignmentRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repImprovementKind:ImprovementKindRepositoryComponentSub): Either[Error, AssignmentSrv]

    def update(token: String, signature: String, pAssignment: AssignmentSrv)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, AssignmentSelect]

    def delete(token: String, signature: String)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, Unit]

    def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, List[AssignmentSrv]]

    
    def findBySignature(token: String, signature: String)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, Option[AssignmentSelect]]
            def findByImprovement(token: String, signature: String)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, List[AssignmentSrv]]
        def findByImprovementKind(token: String, signature: String)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, List[AssignmentSrv]]
        
}

abstract class AssignmentServices extends AssignmentServicesComponent {

    override def create(token : String,pAssignment: AssignmentSrv)(implicit repository: AssignmentRepositoryComponentSub, repImprovement:ImprovementRepositoryComponentSub, repImprovementKind:ImprovementKindRepositoryComponentSub): Either[Error, AssignmentSrv] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pAssignment.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pAssignment.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pAssignment.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pAssignment.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pAssignment.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
        return Left(ErrorBuilder.invalidForm(ERROR_INVALID_ASSIGNMENT_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

                
        val improvement: ImprovementDB = repImprovement.findIfAllowed(profile.id, pAssignment.improvementSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_ASSIGNMENT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_ASSIGNMENT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementSig", CommonError.NOT_ALLOWED))))
            }
        }
                        
        val improvementkind: ImprovementKindDB = repImprovementKind.findIfAllowed(profile.id, pAssignment.improvementkindSig) match {
            case Left(_) => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_ASSIGNMENT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementkindSig", CommonError.NOT_ALLOWED))))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.invalidForm(ERROR_INVALID_ASSIGNMENT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("improvementkindSig", CommonError.NOT_ALLOWED))))
            }
        }
                
        val assignmentDB = AssignmentDB(

                    
                idImprovement = improvement.id,
                                
                idImprKind = improvementkind.id,
                            
            
                signature = new Slugify().slugify(pAssignment.name),
                                        
                 name = pAssignment.name ,
                                
                 description = Option(pAssignment.description) ,
                            )
        
        repository.create(assignmentDB) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(dbToSrv(ret))
        }
    }

    override def update(token: String, signature: String, pAssignment: AssignmentSrv)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, AssignmentSelect] = {

        val fieldErrors = ArrayBuffer.empty[Error]
        //if (isSignatureTooLong(pAssignment.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_long"))
        //if (isSignatureTooShort(pAssignment.signature)) fieldErrors.append(ErrorBuilder.fieldError("signature", "signature_too_short"))
        //if (isNameTooLong(pAssignment.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_long"))
        //if (isNameTooShort(pAssignment.name)) fieldErrors.append(ErrorBuilder.fieldError("name", "name_too_short"))
        //if (isDescriptionTooLong(pAssignment.description)) fieldErrors.append(ErrorBuilder.fieldError("description", "description_too_long"))
        if (!fieldErrors.isEmpty) {
            return Left(ErrorBuilder.invalidForm(ERROR_INVALID_ASSIGNMENT_FORM, "Please verify the form", fieldErrors.toList))
        }

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val assignmentDB:AssignmentDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_ASSIGNMENT_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_ASSIGNMENT_NOT_ALLOWED, "Signature="+signature))
            }
        }

                                        
        val updatedAssignment = AssignmentDB(
                                                        
                signature = new Slugify().slugify(pAssignment.name),
                            
                 name = pAssignment.name ,
                                
                 description = Option(pAssignment.description) ,
                            )

        repository.update(assignmentDB.id, updatedAssignment) match {
            case Left(ret) => Left(transformError(ret))
            case Right(ret) => Right(ret)
        }
    }

    override def delete(token: String, signature: String)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, Unit] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        val assignmentDB:AssignmentDB = repository.findIfAllowed(profile.id, signature) match {
            case Left(_) => return Left(ErrorBuilder.notAllowed(UPDATE_ASSIGNMENT_NOT_ALLOWED, "Profile id="+profile.id+", signature="+signature))
            case Right(r) => r match {
                case Some(l) =>  l
                case None => return Left(ErrorBuilder.notFound(UPDATE_ASSIGNMENT_NOT_ALLOWED, "Signature="+signature))
            }
        }
        repository.delete(assignmentDB.id)
    }

    override def findByProfile(token: String, index: Int, nb: Int, col:String=null, value:String=null)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, List[AssignmentSrv]] = {

        val profile: Profile = Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
            case Right(prf) => prf
        }

        repository.findByProfile(profile.id, index, nb, col, value) match {
            case Left(error) => Left(error)
            case Right(assignments) => Right(assignments.map(ws => dbToSrv(ws)))
        }
    }


    def findBySignature(token: String, signature: String)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, Option[AssignmentSelect]] = {
        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }
        repository.findBySignature(signature)
    }

    def findByImprovement(token: String, signature: String)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, List[AssignmentSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByImprovement(signature) match {
            case Left(error) => Left(error)
            case Right(assignments) => Right(assignments.map(ws => dbToSrv(ws)))
        }
    }
    def findByImprovementKind(token: String, signature: String)(implicit repository: AssignmentRepositoryComponentSub): Either[Error, List[AssignmentSrv]] = {

        Authenticate.findOrCreateProfile(token) match {
            case Left(e) => return Left(e)
        }

        repository.findByImprovementKind(signature) match {
            case Left(error) => Left(error)
            case Right(assignments) => Right(assignments.map(ws => dbToSrv(ws)))
        }
    }


private def transformError(error: Error): Error = {
error.code match {
case CommonError.SQL_CONSTRAINT_VIOLATION => ErrorBuilder.invalidForm(ERROR_INVALID_ASSIGNMENT_FORM, "Please verify the form", List(ErrorBuilder.fieldError("name", CommonError.ALREADY_EXISTS)))
case _ => error
}
}

private def isSignatureTooLong(signature: String): Boolean = SIGNATURE_MAX_LENGTH < signature.length

private def isSignatureTooShort(signature: String): Boolean = SIGNATURE_MIN_LENGTH > signature.length

private def isNameTooLong(name: String): Boolean = NAME_MAX_LENGTH < name.length

private def isNameTooShort(name: String): Boolean = NAME_MIN_LENGTH > name.length

private def isDescriptionTooLong(description: String): Boolean = DESCRIPTION_MAX_LENGTH < description.length

private def dbToSrv(pAssignment: AssignmentSelect): AssignmentSrv = {
AssignmentSrv(
     improvementName =  pAssignment.improvementName ,
     improvementSig =  pAssignment.improvementSig ,
     improvementkindName =  pAssignment.improvementkindName ,
     improvementkindSig =  pAssignment.improvementkindSig ,

    signature = pAssignment.signature,
     name=pAssignment.name,
         description=pAssignment.description.getOrElse(null),
    
    createdDate = pAssignment.createdDate,

    updatedDate = pAssignment.updatedDate.orNull,
)
}
}
