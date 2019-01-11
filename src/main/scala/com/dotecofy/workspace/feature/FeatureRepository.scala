

package com.dotecofy.workspace.feature

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class FeatureDB(

    id:Int = 0,

    signature:String = null,
     idProject: Int = 0,
         name: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class FeatureSelect(

    signature:String = null,
     projectName: String = null,
     projectSig: String = null,
     name: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait FeatureRepositoryComponent {

def create(feature: FeatureDB)(implicit conn: Connection): Either[Error, FeatureSelect]

def update(id: Int, feature: FeatureDB)(implicit conn: Connection): Either[Error, FeatureSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[FeatureSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[FeatureDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[FeatureSelect]]

def findByProject(signature: String)(implicit conn: Connection): Either[Error, List[FeatureSelect]]


}

abstract class FeatureRepository extends FeatureRepositoryComponent {

    override def create(feature: FeatureDB)(implicit conn: Connection): Either[Error, FeatureSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO feature(signature,id_project,name,description,created_date) VALUES (?,?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,feature.signature)
stmt.setInt(2,feature.idProject)
stmt.setString(3,feature.name)
stmt.setString(4,feature.description.getOrElse(""))
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

    override def update(id: Int, feature: FeatureDB)(implicit conn: Connection): Either[Error, FeatureSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE feature SET signature=?,name=?,description=?,updated_date=? WHERE id=?")

            stmt.setString(1,feature.signature)
stmt.setString(2,feature.name)
stmt.setString(3,feature.description.getOrElse(""))
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
            val stmt = conn.prepareStatement("DELETE FROM feature WHERE feature.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[FeatureSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT feature.*,project.name as projectName,project.signature as projectSig FROM feature INNER JOIN project ON feature.id_project=project.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT feature.*,project.name as projectName,project.signature as projectSig FROM feature INNER JOIN project ON feature.id_project=project.id WHERE feature.signature=?")
                    stmt.setString(1, value)
                }
                                case "name" => {
                    stmt = conn.prepareStatement("SELECT feature.*,project.name as projectName,project.signature as projectSig FROM feature INNER JOIN project ON feature.id_project=project.id WHERE feature.name=?")
                    stmt.setString(1, value)
                }
                                case "description" => {
                    stmt = conn.prepareStatement("SELECT feature.*,project.name as projectName,project.signature as projectSig FROM feature INNER JOIN project ON feature.id_project=project.id WHERE feature.description=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[FeatureSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT feature.*,project.name as projectName,project.signature as projectSig FROM feature INNER JOIN project ON feature.id_project=project.id WHERE feature.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[FeatureSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT feature.*,project.name as projectName,project.signature as projectSig FROM feature INNER JOIN project ON feature.id_project=project.id WHERE feature.signature=?")
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
       
        override def findByProject(signature: String)(implicit conn: Connection): Either[Error, List[FeatureSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT feature.*,project.name as projectName,project.signature as projectSig FROM feature INNER JOIN project ON feature.id_project=project.id WHERE project.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[FeatureDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT feature.*,project.name as projectName,project.signature as projectSig FROM feature INNER JOIN project ON feature.id_project=project.id WHERE feature.signature=?")
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

    private def applyList(rs: ResultSet): List[FeatureSelect] = {
        val res : ListBuffer[FeatureSelect] = ListBuffer.empty[FeatureSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): FeatureSelect = FeatureSelect(
        
        signature = rs.getString("signature"),
                        projectName = rs.getString("projectName"),
        projectSig = rs.getString("projectSig"),
                name = 
            rs.getString("name"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): FeatureDB = FeatureDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idProject = rs.getInt("id_project"),
                name = 
            rs.getString("name"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
