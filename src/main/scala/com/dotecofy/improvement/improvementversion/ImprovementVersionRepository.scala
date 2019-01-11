

package com.dotecofy.improvement.improvementversion

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class ImprovementVersionDB(

    id:Int = 0,

    signature:String = null,
     idImprovement: Int = 0,
         idVersion: Int = 0,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class ImprovementVersionSelect(

    signature:String = null,
     improvementName: String = null,
     improvementSig: String = null,
     versionName: String = null,
     versionSig: String = null,

    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait ImprovementVersionRepositoryComponent {

def create(improvementversion: ImprovementVersionDB)(implicit conn: Connection): Either[Error, ImprovementVersionSelect]

def update(id: Int, improvementversion: ImprovementVersionDB)(implicit conn: Connection): Either[Error, ImprovementVersionSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementVersionSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementVersionDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementVersionSelect]]

def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementVersionSelect]]
def findByVersion(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementVersionSelect]]


}

abstract class ImprovementVersionRepository extends ImprovementVersionRepositoryComponent {

    override def create(improvementversion: ImprovementVersionDB)(implicit conn: Connection): Either[Error, ImprovementVersionSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO improvement_version(signature,id_improvement,id_version,created_date) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,improvementversion.signature)
stmt.setInt(2,improvementversion.idImprovement)
stmt.setInt(3,improvementversion.idVersion)
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

    override def update(id: Int, improvementversion: ImprovementVersionDB)(implicit conn: Connection): Either[Error, ImprovementVersionSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE improvement_version SET signature=?,updated_date=? WHERE id=?")

            stmt.setString(1,improvementversion.signature)
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
            val stmt = conn.prepareStatement("DELETE FROM improvement_version WHERE improvement_version.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementVersionSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT improvement_version.*,improvement.name as improvementName,improvement.signature as improvementSig,version.name as versionName,version.signature as versionSig FROM improvement_version INNER JOIN improvement ON improvement_version.id_improvement=improvement.id INNER JOIN version ON improvement_version.id_version=version.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT improvement_version.*,improvement.name as improvementName,improvement.signature as improvementSig,version.name as versionName,version.signature as versionSig FROM improvement_version INNER JOIN improvement ON improvement_version.id_improvement=improvement.id INNER JOIN version ON improvement_version.id_version=version.id WHERE improvement_version.signature=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[ImprovementVersionSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_version.*,improvement.name as improvementName,improvement.signature as improvementSig,version.name as versionName,version.signature as versionSig FROM improvement_version INNER JOIN improvement ON improvement_version.id_improvement=improvement.id INNER JOIN version ON improvement_version.id_version=version.id WHERE improvement_version.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementVersionSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_version.*,improvement.name as improvementName,improvement.signature as improvementSig,version.name as versionName,version.signature as versionSig FROM improvement_version INNER JOIN improvement ON improvement_version.id_improvement=improvement.id INNER JOIN version ON improvement_version.id_version=version.id WHERE improvement_version.signature=?")
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
       
        override def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementVersionSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_version.*,improvement.name as improvementName,improvement.signature as improvementSig,version.name as versionName,version.signature as versionSig FROM improvement_version INNER JOIN improvement ON improvement_version.id_improvement=improvement.id INNER JOIN version ON improvement_version.id_version=version.id WHERE improvement.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
        override def findByVersion(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementVersionSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_version.*,improvement.name as improvementName,improvement.signature as improvementSig,version.name as versionName,version.signature as versionSig FROM improvement_version INNER JOIN improvement ON improvement_version.id_improvement=improvement.id INNER JOIN version ON improvement_version.id_version=version.id WHERE version.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementVersionDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_version.*,improvement.name as improvementName,improvement.signature as improvementSig,version.name as versionName,version.signature as versionSig FROM improvement_version INNER JOIN improvement ON improvement_version.id_improvement=improvement.id INNER JOIN version ON improvement_version.id_version=version.id WHERE improvement_version.signature=?")
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

    private def applyList(rs: ResultSet): List[ImprovementVersionSelect] = {
        val res : ListBuffer[ImprovementVersionSelect] = ListBuffer.empty[ImprovementVersionSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): ImprovementVersionSelect = ImprovementVersionSelect(
        
        signature = rs.getString("signature"),
                        improvementName = rs.getString("improvementName"),
        improvementSig = rs.getString("improvementSig"),
                versionName = rs.getString("versionName"),
        versionSig = rs.getString("versionSig"),
                        
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): ImprovementVersionDB = ImprovementVersionDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idImprovement = rs.getInt("id_improvement"),
                idVersion = rs.getInt("id_version"),
                        
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
