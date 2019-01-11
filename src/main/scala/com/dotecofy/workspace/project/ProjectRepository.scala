

package com.dotecofy.workspace.project

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class ProjectDB(

    id:Int = 0,

    signature:String = null,
     idWorkspace: Int = 0,
         name: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class ProjectSelect(

    signature:String = null,
     workspaceName: String = null,
     workspaceSig: String = null,
     name: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait ProjectRepositoryComponent {

def create(project: ProjectDB)(implicit conn: Connection): Either[Error, ProjectSelect]

def update(id: Int, project: ProjectDB)(implicit conn: Connection): Either[Error, ProjectSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ProjectSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ProjectDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ProjectSelect]]

def findByWorkspace(signature: String)(implicit conn: Connection): Either[Error, List[ProjectSelect]]

        
}

abstract class ProjectRepository extends ProjectRepositoryComponent {

    override def create(project: ProjectDB)(implicit conn: Connection): Either[Error, ProjectSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO project(signature,id_workspace,name,description,created_date) VALUES (?,?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,project.signature)
stmt.setInt(2,project.idWorkspace)
stmt.setString(3,project.name)
stmt.setString(4,project.description.getOrElse(""))
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

    override def update(id: Int, project: ProjectDB)(implicit conn: Connection): Either[Error, ProjectSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE project SET signature=?,id_workspace=?,name=?,description=?,updated_date=? WHERE id=?")

            stmt.setString(1,project.signature)
stmt.setInt(2,project.idWorkspace)
stmt.setString(3,project.name)
stmt.setString(4,project.description.getOrElse(""))
stmt.setDate(5, new java.sql.Date(new java.util.Date().getTime))
stmt.setInt(6, id)

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
            val stmt = conn.prepareStatement("DELETE FROM project WHERE project.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ProjectSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT project.*,workspace.name as workspaceName,workspace.signature as workspaceSig FROM project INNER JOIN workspace ON project.id_workspace=workspace.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT project.*,workspace.name as workspaceName,workspace.signature as workspaceSig FROM project INNER JOIN workspace ON project.id_workspace=workspace.id WHERE project.signature=?")
                    stmt.setString(1, value)
                }
                                case "name" => {
                    stmt = conn.prepareStatement("SELECT project.*,workspace.name as workspaceName,workspace.signature as workspaceSig FROM project INNER JOIN workspace ON project.id_workspace=workspace.id WHERE project.name=?")
                    stmt.setString(1, value)
                }
                                case "description" => {
                    stmt = conn.prepareStatement("SELECT project.*,workspace.name as workspaceName,workspace.signature as workspaceSig FROM project INNER JOIN workspace ON project.id_workspace=workspace.id WHERE project.description=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[ProjectSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT project.*,workspace.name as workspaceName,workspace.signature as workspaceSig FROM project INNER JOIN workspace ON project.id_workspace=workspace.id WHERE project.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ProjectSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT project.*,workspace.name as workspaceName,workspace.signature as workspaceSig FROM project INNER JOIN workspace ON project.id_workspace=workspace.id WHERE project.signature=?")
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
       
        override def findByWorkspace(signature: String)(implicit conn: Connection): Either[Error, List[ProjectSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT project.*,workspace.name as workspaceName,workspace.signature as workspaceSig FROM project INNER JOIN workspace ON project.id_workspace=workspace.id WHERE workspace.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
                    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ProjectDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT project.*,workspace.name as workspaceName,workspace.signature as workspaceSig FROM project INNER JOIN workspace ON project.id_workspace=workspace.id WHERE project.signature=?")
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

    private def applyList(rs: ResultSet): List[ProjectSelect] = {
        val res : ListBuffer[ProjectSelect] = ListBuffer.empty[ProjectSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): ProjectSelect = ProjectSelect(
        
        signature = rs.getString("signature"),
                        workspaceName = rs.getString("workspaceName"),
        workspaceSig = rs.getString("workspaceSig"),
                name = 
            rs.getString("name"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): ProjectDB = ProjectDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idWorkspace = rs.getInt("id_workspace"),
                name = 
            rs.getString("name"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
