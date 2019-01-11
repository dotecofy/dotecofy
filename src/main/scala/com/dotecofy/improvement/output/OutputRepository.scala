

package com.dotecofy.improvement.output

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class OutputDB(

    id:Int = 0,

    signature:String = null,
     idAssignment: Int = 0,
         idImprCycle: Int = 0,
         remark: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class OutputSelect(

    signature:String = null,
     assignmentName: String = null,
     assignmentSig: String = null,
     improvementcycleName: String = null,
     improvementcycleSig: String = null,
     remark: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait OutputRepositoryComponent {

def create(output: OutputDB)(implicit conn: Connection): Either[Error, OutputSelect]

def update(id: Int, output: OutputDB)(implicit conn: Connection): Either[Error, OutputSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[OutputSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[OutputDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[OutputSelect]]

def findByAssignment(signature: String)(implicit conn: Connection): Either[Error, List[OutputSelect]]
def findByImprovementCycle(signature: String)(implicit conn: Connection): Either[Error, List[OutputSelect]]


}

abstract class OutputRepository extends OutputRepositoryComponent {

    override def create(output: OutputDB)(implicit conn: Connection): Either[Error, OutputSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO output(signature,id_assignment,id_impr_cycle,remark,created_date) VALUES (?,?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,output.signature)
stmt.setInt(2,output.idAssignment)
stmt.setInt(3,output.idImprCycle)
stmt.setString(4,output.remark.getOrElse(""))
stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime))

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

    override def update(id: Int, output: OutputDB)(implicit conn: Connection): Either[Error, OutputSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE output SET signature=?,remark=?,updated_date=? WHERE id=?")

            stmt.setString(1,output.signature)
stmt.setString(2,output.remark.getOrElse(""))
stmt.setDate(3, new java.sql.Date(new java.util.Date().getTime))
stmt.setInt(4, id)

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
            val stmt = conn.prepareStatement("DELETE FROM output WHERE output.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[OutputSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT output.*,assignment.name as assignmentName,assignment.signature as assignmentSig,improvement_cycle.name as improvementcycleName,improvement_cycle.signature as improvementcycleSig FROM output INNER JOIN assignment ON output.id_assignment=assignment.id INNER JOIN improvement_cycle ON output.id_impr_cycle=improvement_cycle.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT output.*,assignment.name as assignmentName,assignment.signature as assignmentSig,improvement_cycle.name as improvementcycleName,improvement_cycle.signature as improvementcycleSig FROM output INNER JOIN assignment ON output.id_assignment=assignment.id INNER JOIN improvement_cycle ON output.id_impr_cycle=improvement_cycle.id WHERE output.signature=?")
                    stmt.setString(1, value)
                }
                                case "remark" => {
                    stmt = conn.prepareStatement("SELECT output.*,assignment.name as assignmentName,assignment.signature as assignmentSig,improvement_cycle.name as improvementcycleName,improvement_cycle.signature as improvementcycleSig FROM output INNER JOIN assignment ON output.id_assignment=assignment.id INNER JOIN improvement_cycle ON output.id_impr_cycle=improvement_cycle.id WHERE output.remark=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[OutputSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT output.*,assignment.name as assignmentName,assignment.signature as assignmentSig,improvement_cycle.name as improvementcycleName,improvement_cycle.signature as improvementcycleSig FROM output INNER JOIN assignment ON output.id_assignment=assignment.id INNER JOIN improvement_cycle ON output.id_impr_cycle=improvement_cycle.id WHERE output.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[OutputSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT output.*,assignment.name as assignmentName,assignment.signature as assignmentSig,improvement_cycle.name as improvementcycleName,improvement_cycle.signature as improvementcycleSig FROM output INNER JOIN assignment ON output.id_assignment=assignment.id INNER JOIN improvement_cycle ON output.id_impr_cycle=improvement_cycle.id WHERE output.signature=?")
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
       
        override def findByAssignment(signature: String)(implicit conn: Connection): Either[Error, List[OutputSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT output.*,assignment.name as assignmentName,assignment.signature as assignmentSig,improvement_cycle.name as improvementcycleName,improvement_cycle.signature as improvementcycleSig FROM output INNER JOIN assignment ON output.id_assignment=assignment.id INNER JOIN improvement_cycle ON output.id_impr_cycle=improvement_cycle.id WHERE assignment.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
        override def findByImprovementCycle(signature: String)(implicit conn: Connection): Either[Error, List[OutputSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT output.*,assignment.name as assignmentName,assignment.signature as assignmentSig,improvement_cycle.name as improvementcycleName,improvement_cycle.signature as improvementcycleSig FROM output INNER JOIN assignment ON output.id_assignment=assignment.id INNER JOIN improvement_cycle ON output.id_impr_cycle=improvement_cycle.id WHERE improvement_cycle.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[OutputDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT output.*,assignment.name as assignmentName,assignment.signature as assignmentSig,improvement_cycle.name as improvementcycleName,improvement_cycle.signature as improvementcycleSig FROM output INNER JOIN assignment ON output.id_assignment=assignment.id INNER JOIN improvement_cycle ON output.id_impr_cycle=improvement_cycle.id WHERE output.signature=?")
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

    private def applyList(rs: ResultSet): List[OutputSelect] = {
        val res : ListBuffer[OutputSelect] = ListBuffer.empty[OutputSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): OutputSelect = OutputSelect(
        
        signature = rs.getString("signature"),
                        assignmentName = rs.getString("assignmentName"),
        assignmentSig = rs.getString("assignmentSig"),
                improvementcycleName = rs.getString("improvementcycleName"),
        improvementcycleSig = rs.getString("improvementcycleSig"),
                remark = 
             Option(rs.getString("remark")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): OutputDB = OutputDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idAssignment = rs.getInt("id_assignment"),
                idImprCycle = rs.getInt("id_impr_cycle"),
                remark = 
             Option(rs.getString("remark")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
