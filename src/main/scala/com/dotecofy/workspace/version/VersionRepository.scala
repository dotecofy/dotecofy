

package com.dotecofy.workspace.version

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class VersionDB(

    id:Int = 0,

    signature:String = null,
     idProject: Int = 0,
         version: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class VersionSelect(

    signature:String = null,
     projectName: String = null,
     projectSig: String = null,
     version: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait VersionRepositoryComponent {

def create(version: VersionDB)(implicit conn: Connection): Either[Error, VersionSelect]

def update(id: Int, version: VersionDB)(implicit conn: Connection): Either[Error, VersionSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[VersionSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[VersionDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[VersionSelect]]

def findByProject(signature: String)(implicit conn: Connection): Either[Error, List[VersionSelect]]

    
def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[VersionSelect]]
    
}

abstract class VersionRepository extends VersionRepositoryComponent {

    override def create(version: VersionDB)(implicit conn: Connection): Either[Error, VersionSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO version(signature,id_project,version,description,created_date) VALUES (?,?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,version.signature)
stmt.setInt(2,version.idProject)
stmt.setString(3,version.version)
stmt.setString(4,version.description.getOrElse(""))
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

    override def update(id: Int, version: VersionDB)(implicit conn: Connection): Either[Error, VersionSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE version SET signature=?,version=?,description=?,updated_date=? WHERE id=?")

            stmt.setString(1,version.signature)
stmt.setString(2,version.version)
stmt.setString(3,version.description.getOrElse(""))
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
            val stmt = conn.prepareStatement("DELETE FROM version WHERE version.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[VersionSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id WHERE version.signature=?")
                    stmt.setString(1, value)
                }
                                case "version" => {
                    stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id WHERE version.version=?")
                    stmt.setString(1, value)
                }
                                case "description" => {
                    stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id WHERE version.description=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[VersionSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id WHERE version.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[VersionSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id WHERE version.signature=?")
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
       
        override def findByProject(signature: String)(implicit conn: Connection): Either[Error, List[VersionSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id WHERE project.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
        
    override def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[VersionSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id INNER JOIN improvement_version ON version.id=improvement_version.id_improvement INNER JOIN improvement ON improvement_version.id_improvement=improvement.id WHERE improvement.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
        
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[VersionDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT version.*,project.name as projectName,project.signature as projectSig FROM version INNER JOIN project ON version.id_project=project.id WHERE version.signature=?")
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

    private def applyList(rs: ResultSet): List[VersionSelect] = {
        val res : ListBuffer[VersionSelect] = ListBuffer.empty[VersionSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): VersionSelect = VersionSelect(
        
        signature = rs.getString("signature"),
                        projectName = rs.getString("projectName"),
        projectSig = rs.getString("projectSig"),
                version = 
            rs.getString("version"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): VersionDB = VersionDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idProject = rs.getInt("id_project"),
                version = 
            rs.getString("version"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
