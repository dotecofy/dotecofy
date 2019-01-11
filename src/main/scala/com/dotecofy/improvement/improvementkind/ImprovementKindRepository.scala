

package com.dotecofy.improvement.improvementkind

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class ImprovementKindDB(

    id:Int = 0,

    signature:String = null,
     idImprovement: Int = 0,
         idKind: Int = 0,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class ImprovementKindSelect(

    signature:String = null,
     improvementName: String = null,
     improvementSig: String = null,
     kindName: String = null,
     kindSig: String = null,

    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait ImprovementKindRepositoryComponent {

def create(improvementkind: ImprovementKindDB)(implicit conn: Connection): Either[Error, ImprovementKindSelect]

def update(id: Int, improvementkind: ImprovementKindDB)(implicit conn: Connection): Either[Error, ImprovementKindSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementKindSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementKindDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementKindSelect]]

def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementKindSelect]]
def findByKind(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementKindSelect]]


}

abstract class ImprovementKindRepository extends ImprovementKindRepositoryComponent {

    override def create(improvementkind: ImprovementKindDB)(implicit conn: Connection): Either[Error, ImprovementKindSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO improvement_kind(signature,id_improvement,id_kind,created_date) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,improvementkind.signature)
stmt.setInt(2,improvementkind.idImprovement)
stmt.setInt(3,improvementkind.idKind)
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

    override def update(id: Int, improvementkind: ImprovementKindDB)(implicit conn: Connection): Either[Error, ImprovementKindSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE improvement_kind SET signature=?,updated_date=? WHERE id=?")

            stmt.setString(1,improvementkind.signature)
stmt.setDate(2, new java.sql.Date(new java.util.Date().getTime))
stmt.setInt(3, id)

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
            val stmt = conn.prepareStatement("DELETE FROM improvement_kind WHERE improvement_kind.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementKindSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT improvement_kind.*,improvement.name as improvementName,improvement.signature as improvementSig,kind.name as kindName,kind.signature as kindSig FROM improvement_kind INNER JOIN improvement ON improvement_kind.id_improvement=improvement.id INNER JOIN kind ON improvement_kind.id_kind=kind.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT improvement_kind.*,improvement.name as improvementName,improvement.signature as improvementSig,kind.name as kindName,kind.signature as kindSig FROM improvement_kind INNER JOIN improvement ON improvement_kind.id_improvement=improvement.id INNER JOIN kind ON improvement_kind.id_kind=kind.id WHERE improvement_kind.signature=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[ImprovementKindSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_kind.*,improvement.name as improvementName,improvement.signature as improvementSig,kind.name as kindName,kind.signature as kindSig FROM improvement_kind INNER JOIN improvement ON improvement_kind.id_improvement=improvement.id INNER JOIN kind ON improvement_kind.id_kind=kind.id WHERE improvement_kind.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementKindSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_kind.*,improvement.name as improvementName,improvement.signature as improvementSig,kind.name as kindName,kind.signature as kindSig FROM improvement_kind INNER JOIN improvement ON improvement_kind.id_improvement=improvement.id INNER JOIN kind ON improvement_kind.id_kind=kind.id WHERE improvement_kind.signature=?")
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
       
        override def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementKindSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_kind.*,improvement.name as improvementName,improvement.signature as improvementSig,kind.name as kindName,kind.signature as kindSig FROM improvement_kind INNER JOIN improvement ON improvement_kind.id_improvement=improvement.id INNER JOIN kind ON improvement_kind.id_kind=kind.id WHERE improvement.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
        override def findByKind(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementKindSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_kind.*,improvement.name as improvementName,improvement.signature as improvementSig,kind.name as kindName,kind.signature as kindSig FROM improvement_kind INNER JOIN improvement ON improvement_kind.id_improvement=improvement.id INNER JOIN kind ON improvement_kind.id_kind=kind.id WHERE kind.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementKindDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_kind.*,improvement.name as improvementName,improvement.signature as improvementSig,kind.name as kindName,kind.signature as kindSig FROM improvement_kind INNER JOIN improvement ON improvement_kind.id_improvement=improvement.id INNER JOIN kind ON improvement_kind.id_kind=kind.id WHERE improvement_kind.signature=?")
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

    private def applyList(rs: ResultSet): List[ImprovementKindSelect] = {
        val res : ListBuffer[ImprovementKindSelect] = ListBuffer.empty[ImprovementKindSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): ImprovementKindSelect = ImprovementKindSelect(
        
        signature = rs.getString("signature"),
                        improvementName = rs.getString("improvementName"),
        improvementSig = rs.getString("improvementSig"),
                kindName = rs.getString("kindName"),
        kindSig = rs.getString("kindSig"),
                        
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): ImprovementKindDB = ImprovementKindDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idImprovement = rs.getInt("id_improvement"),
                idKind = rs.getInt("id_kind"),
                        
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
