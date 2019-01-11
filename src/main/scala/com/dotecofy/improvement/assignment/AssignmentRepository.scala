

package com.dotecofy.improvement.assignment

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class AssignmentDB(

    id:Int = 0,

    signature:String = null,
     idImprovement: Int = 0,
         idImprKind: Int = 0,
         name: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class AssignmentSelect(

    signature:String = null,
     improvementName: String = null,
     improvementSig: String = null,
     improvementkindName: String = null,
     improvementkindSig: String = null,
     name: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait AssignmentRepositoryComponent {

def create(assignment: AssignmentDB)(implicit conn: Connection): Either[Error, AssignmentSelect]

def update(id: Int, assignment: AssignmentDB)(implicit conn: Connection): Either[Error, AssignmentSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[AssignmentSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[AssignmentDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[AssignmentSelect]]

def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[AssignmentSelect]]
def findByImprovementKind(signature: String)(implicit conn: Connection): Either[Error, List[AssignmentSelect]]


}

abstract class AssignmentRepository extends AssignmentRepositoryComponent {

    override def create(assignment: AssignmentDB)(implicit conn: Connection): Either[Error, AssignmentSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO assignment(signature,id_improvement,id_impr_kind,name,description,created_date) VALUES (?,?,?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,assignment.signature)
stmt.setInt(2,assignment.idImprovement)
stmt.setInt(3,assignment.idImprKind)
stmt.setString(4,assignment.name)
stmt.setString(5,assignment.description.getOrElse(""))
stmt.setDate(6, new java.sql.Date(new java.util.Date().getTime))

            val affectedRows = stmt.executeUpdate

            if (affectedRows == 0) throw new SQLException("Creating profile failed, no rows affected.")

            val generatedKeys = stmt.getGeneratedKeys
            try
                if (generatedKeys.next) {
                    val findW = find(generatedKeys.getInt(1))
                    findW match {
                        case Some(ret) => Right(ret)
                        case None => Left(ErrorBuilder.internalError(CommonError.ERROR_COULD_NOT_INSERT, "The generated key is not found in database"))
                    }
                }
                else throw new SQLException("Creating profile failed, no ID obtained.")
            finally if (generatedKeys != null) generatedKeys.close()
        } catch {
            case integrity: SQLIntegrityConstraintViolationException => Left(Error(ErrorBuilder.SQL_EXCEPTION, CommonError.SQL_CONSTRAINT_VIOLATION, Option(integrity.getMessage), Option(List())))
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }

    override def update(id: Int, assignment: AssignmentDB)(implicit conn: Connection): Either[Error, AssignmentSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE assignment SET signature=?,name=?,description=?,updated_date=? WHERE id=?")

            stmt.setString(1,assignment.signature)
stmt.setString(2,assignment.name)
stmt.setString(3,assignment.description.getOrElse(""))
stmt.setDate(4, new java.sql.Date(new java.util.Date().getTime))
stmt.setInt(5, id)

            val affectedRows = stmt.executeUpdate

            if (affectedRows == 0) {
                throw new SQLException("Creating profile failed, no rows affected.")
            }else {
                val findW = find(id)
                findW match {
                    case Some(ret) => Right(ret)
                    case None => Left(ErrorBuilder.internalError(CommonError.ERROR_COULD_NOT_INSERT, "The generated key is not found in database"))
                }
            }

        } catch {
            case integrity: SQLIntegrityConstraintViolationException => Left(Error(ErrorBuilder.SQL_EXCEPTION, CommonError.SQL_CONSTRAINT_VIOLATION, Option(integrity.getMessage), Option(List())))
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }

    override def delete(id: Int)(implicit conn: Connection): Either[Error, Unit] = {
        try {
            val stmt = conn.prepareStatement("DELETE FROM assignment WHERE assignment.id=?")
            stmt.setInt(1, id)

            val affectedRows = stmt.executeUpdate
            if (affectedRows == 0) {
                throw new SQLException("Creating profile failed, no rows affected.")
            }
            Right()
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[AssignmentSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id WHERE assignment.signature=?")
                    stmt.setString(1, value)
                }
                                case "name" => {
                    stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id WHERE assignment.name=?")
                    stmt.setString(1, value)
                }
                                case "description" => {
                    stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id WHERE assignment.description=?")
                    stmt.setString(1, value)
                }
                            }
            
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }

    }

    private def find(id: Int)(implicit conn: Connection): Option[AssignmentSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id WHERE assignment.id=?")
            stmt.setInt(1, id)
            val rs:ResultSet = stmt.executeQuery

            if(rs.next) Option(apply(rs))
            else None
            
        } catch {
            case sql: SQLException => None
        } finally {
            conn.close()
        }
    }

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[AssignmentSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id WHERE assignment.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            if(rs.next) Right(Option(apply(rs)))
            else Right(None)
        
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
       
        override def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[AssignmentSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id WHERE improvement.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
        override def findByImprovementKind(signature: String)(implicit conn: Connection): Either[Error, List[AssignmentSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id WHERE improvement_kind.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[AssignmentDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT assignment.*,improvement.name as improvementName,improvement.signature as improvementSig,improvement_kind.name as improvementkindName,improvement_kind.signature as improvementkindSig FROM assignment INNER JOIN improvement ON assignment.id_improvement=improvement.id INNER JOIN improvement_kind ON assignment.id_impr_kind=improvement_kind.id WHERE assignment.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            if(rs.next) Right(Option(applyDB(rs)))
            else Right(None)
            
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }

    private def applyList(rs: ResultSet): List[AssignmentSelect] = {
        val res : ListBuffer[AssignmentSelect] = ListBuffer.empty[AssignmentSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): AssignmentSelect = AssignmentSelect(
        
        signature = rs.getString("signature"),
                        improvementName = rs.getString("improvementName"),
        improvementSig = rs.getString("improvementSig"),
                improvementkindName = rs.getString("improvementkindName"),
        improvementkindSig = rs.getString("improvementkindSig"),
                name = 
            rs.getString("name"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): AssignmentDB = AssignmentDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idImprovement = rs.getInt("id_improvement"),
                idImprKind = rs.getInt("id_impr_kind"),
                name = 
            rs.getString("name"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
