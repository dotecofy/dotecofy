

package com.dotecofy.improvement.improvement

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class ImprovementDB(

    id:Int = 0,

    signature:String = null,
     idFeature: Int = 0,
         name: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class ImprovementSelect(

    signature:String = null,
     featureName: String = null,
     featureSig: String = null,
     name: String,
         description: Option[String] = None,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait ImprovementRepositoryComponent {

def create(improvement: ImprovementDB)(implicit conn: Connection): Either[Error, ImprovementSelect]

def update(id: Int, improvement: ImprovementDB)(implicit conn: Connection): Either[Error, ImprovementSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementSelect]]

def findByFeature(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]]

        
def findByCycle(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]]
        
def findByLayer(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]]
        
def findByVersion(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]]
        
def findByKind(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]]
    
}

abstract class ImprovementRepository extends ImprovementRepositoryComponent {

    override def create(improvement: ImprovementDB)(implicit conn: Connection): Either[Error, ImprovementSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO improvement(signature,id_feature,name,description,created_date) VALUES (?,?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,improvement.signature)
stmt.setInt(2,improvement.idFeature)
stmt.setString(3,improvement.name)
stmt.setString(4,improvement.description.getOrElse(""))
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

    override def update(id: Int, improvement: ImprovementDB)(implicit conn: Connection): Either[Error, ImprovementSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE improvement SET signature=?,name=?,description=?,updated_date=? WHERE id=?")

            stmt.setString(1,improvement.signature)
stmt.setString(2,improvement.name)
stmt.setString(3,improvement.description.getOrElse(""))
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
            val stmt = conn.prepareStatement("DELETE FROM improvement WHERE improvement.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id WHERE improvement.signature=?")
                    stmt.setString(1, value)
                }
                                case "name" => {
                    stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id WHERE improvement.name=?")
                    stmt.setString(1, value)
                }
                                case "description" => {
                    stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id WHERE improvement.description=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[ImprovementSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id WHERE improvement.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id WHERE improvement.signature=?")
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
       
        override def findByFeature(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id WHERE feature.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
                
    override def findByCycle(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id INNER JOIN improvement_cycle ON improvement.id=improvement_cycle.id_cycle INNER JOIN cycle ON improvement_cycle.id_cycle=cycle.id WHERE cycle.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
            
    override def findByLayer(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id INNER JOIN improvement_layer ON improvement.id=improvement_layer.id_layer INNER JOIN layer ON improvement_layer.id_layer=layer.id WHERE layer.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
            
    override def findByVersion(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id INNER JOIN improvement_version ON improvement.id=improvement_version.id_version INNER JOIN version ON improvement_version.id_version=version.id WHERE version.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
            
    override def findByKind(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id INNER JOIN improvement_kind ON improvement.id=improvement_kind.id_kind INNER JOIN kind ON improvement_kind.id_kind=kind.id WHERE kind.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
        
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement.*,feature.name as featureName,feature.signature as featureSig FROM improvement INNER JOIN feature ON improvement.id_feature=feature.id WHERE improvement.signature=?")
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

    private def applyList(rs: ResultSet): List[ImprovementSelect] = {
        val res : ListBuffer[ImprovementSelect] = ListBuffer.empty[ImprovementSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): ImprovementSelect = ImprovementSelect(
        
        signature = rs.getString("signature"),
                        featureName = rs.getString("featureName"),
        featureSig = rs.getString("featureSig"),
                name = 
            rs.getString("name"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): ImprovementDB = ImprovementDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idFeature = rs.getInt("id_feature"),
                name = 
            rs.getString("name"),
                    description = 
             Option(rs.getString("description")),                 
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
