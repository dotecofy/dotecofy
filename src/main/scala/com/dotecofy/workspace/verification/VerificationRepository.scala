

package com.dotecofy.workspace.verification

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class VerificationDB(

    id:Int = 0,

    signature:String = null,
     idOutput: Int = 0,
         remark: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class VerificationSelect(

    signature:String = null,
     outputName: String = null,
     outputSig: String = null,
     remark: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait VerificationRepositoryComponent {

def create(verification: VerificationDB)(implicit conn: Connection): Either[Error, VerificationSelect]

def update(id: Int, verification: VerificationDB)(implicit conn: Connection): Either[Error, VerificationSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[VerificationSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[VerificationDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[VerificationSelect]]

def findByOutput(signature: String)(implicit conn: Connection): Either[Error, List[VerificationSelect]]


}

abstract class VerificationRepository extends VerificationRepositoryComponent {

    override def create(verification: VerificationDB)(implicit conn: Connection): Either[Error, VerificationSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO verification(signature,id_output,remark,created_date) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,verification.signature)
stmt.setInt(2,verification.idOutput)
stmt.setString(3,verification.remark.getOrElse(""))
stmt.setDate(4, new java.sql.Date(new java.util.Date().getTime))

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

    override def update(id: Int, verification: VerificationDB)(implicit conn: Connection): Either[Error, VerificationSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE verification SET signature=?,remark=?,updated_date=? WHERE id=?")

            stmt.setString(1,verification.signature)
stmt.setString(2,verification.remark.getOrElse(""))
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
            val stmt = conn.prepareStatement("DELETE FROM verification WHERE verification.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[VerificationSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT verification.*,output.name as outputName,output.signature as outputSig FROM verification INNER JOIN output ON verification.id_output=output.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT verification.*,output.name as outputName,output.signature as outputSig FROM verification INNER JOIN output ON verification.id_output=output.id WHERE verification.signature=?")
                    stmt.setString(1, value)
                }
                                case "remark" => {
                    stmt = conn.prepareStatement("SELECT verification.*,output.name as outputName,output.signature as outputSig FROM verification INNER JOIN output ON verification.id_output=output.id WHERE verification.remark=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[VerificationSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT verification.*,output.name as outputName,output.signature as outputSig FROM verification INNER JOIN output ON verification.id_output=output.id WHERE verification.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[VerificationSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT verification.*,output.name as outputName,output.signature as outputSig FROM verification INNER JOIN output ON verification.id_output=output.id WHERE verification.signature=?")
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
       
        override def findByOutput(signature: String)(implicit conn: Connection): Either[Error, List[VerificationSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT verification.*,output.name as outputName,output.signature as outputSig FROM verification INNER JOIN output ON verification.id_output=output.id WHERE output.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[VerificationDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT verification.*,output.name as outputName,output.signature as outputSig FROM verification INNER JOIN output ON verification.id_output=output.id WHERE verification.signature=?")
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

    private def applyList(rs: ResultSet): List[VerificationSelect] = {
        val res : ListBuffer[VerificationSelect] = ListBuffer.empty[VerificationSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): VerificationSelect = VerificationSelect(
        
        signature = rs.getString("signature"),
                        outputName = rs.getString("outputName"),
        outputSig = rs.getString("outputSig"),
                remark = 
             Option(rs.getString("remark")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): VerificationDB = VerificationDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idOutput = rs.getInt("id_output"),
                remark = 
             Option(rs.getString("remark")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
