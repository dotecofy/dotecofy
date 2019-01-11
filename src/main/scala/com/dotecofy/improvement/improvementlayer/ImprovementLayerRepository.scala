

package com.dotecofy.improvement.improvementlayer

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class ImprovementLayerDB(

    id:Int = 0,

    signature:String = null,
     idImprovement: Int = 0,
         idLayer: Int = 0,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class ImprovementLayerSelect(

    signature:String = null,
     improvementName: String = null,
     improvementSig: String = null,
     layerName: String = null,
     layerSig: String = null,

    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait ImprovementLayerRepositoryComponent {

def create(improvementlayer: ImprovementLayerDB)(implicit conn: Connection): Either[Error, ImprovementLayerSelect]

def update(id: Int, improvementlayer: ImprovementLayerDB)(implicit conn: Connection): Either[Error, ImprovementLayerSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementLayerSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementLayerDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementLayerSelect]]

def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementLayerSelect]]
def findByLayer(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementLayerSelect]]


}

abstract class ImprovementLayerRepository extends ImprovementLayerRepositoryComponent {

    override def create(improvementlayer: ImprovementLayerDB)(implicit conn: Connection): Either[Error, ImprovementLayerSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO improvement_layer(signature,id_improvement,id_layer,created_date) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,improvementlayer.signature)
stmt.setInt(2,improvementlayer.idImprovement)
stmt.setInt(3,improvementlayer.idLayer)
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

    override def update(id: Int, improvementlayer: ImprovementLayerDB)(implicit conn: Connection): Either[Error, ImprovementLayerSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE improvement_layer SET signature=?,updated_date=? WHERE id=?")

            stmt.setString(1,improvementlayer.signature)
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
            val stmt = conn.prepareStatement("DELETE FROM improvement_layer WHERE improvement_layer.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementLayerSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT improvement_layer.*,improvement.name as improvementName,improvement.signature as improvementSig,layer.name as layerName,layer.signature as layerSig FROM improvement_layer INNER JOIN improvement ON improvement_layer.id_improvement=improvement.id INNER JOIN layer ON improvement_layer.id_layer=layer.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT improvement_layer.*,improvement.name as improvementName,improvement.signature as improvementSig,layer.name as layerName,layer.signature as layerSig FROM improvement_layer INNER JOIN improvement ON improvement_layer.id_improvement=improvement.id INNER JOIN layer ON improvement_layer.id_layer=layer.id WHERE improvement_layer.signature=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[ImprovementLayerSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_layer.*,improvement.name as improvementName,improvement.signature as improvementSig,layer.name as layerName,layer.signature as layerSig FROM improvement_layer INNER JOIN improvement ON improvement_layer.id_improvement=improvement.id INNER JOIN layer ON improvement_layer.id_layer=layer.id WHERE improvement_layer.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementLayerSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_layer.*,improvement.name as improvementName,improvement.signature as improvementSig,layer.name as layerName,layer.signature as layerSig FROM improvement_layer INNER JOIN improvement ON improvement_layer.id_improvement=improvement.id INNER JOIN layer ON improvement_layer.id_layer=layer.id WHERE improvement_layer.signature=?")
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
       
        override def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementLayerSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_layer.*,improvement.name as improvementName,improvement.signature as improvementSig,layer.name as layerName,layer.signature as layerSig FROM improvement_layer INNER JOIN improvement ON improvement_layer.id_improvement=improvement.id INNER JOIN layer ON improvement_layer.id_layer=layer.id WHERE improvement.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
        override def findByLayer(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementLayerSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_layer.*,improvement.name as improvementName,improvement.signature as improvementSig,layer.name as layerName,layer.signature as layerSig FROM improvement_layer INNER JOIN improvement ON improvement_layer.id_improvement=improvement.id INNER JOIN layer ON improvement_layer.id_layer=layer.id WHERE layer.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementLayerDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_layer.*,improvement.name as improvementName,improvement.signature as improvementSig,layer.name as layerName,layer.signature as layerSig FROM improvement_layer INNER JOIN improvement ON improvement_layer.id_improvement=improvement.id INNER JOIN layer ON improvement_layer.id_layer=layer.id WHERE improvement_layer.signature=?")
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

    private def applyList(rs: ResultSet): List[ImprovementLayerSelect] = {
        val res : ListBuffer[ImprovementLayerSelect] = ListBuffer.empty[ImprovementLayerSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): ImprovementLayerSelect = ImprovementLayerSelect(
        
        signature = rs.getString("signature"),
                        improvementName = rs.getString("improvementName"),
        improvementSig = rs.getString("improvementSig"),
                layerName = rs.getString("layerName"),
        layerSig = rs.getString("layerSig"),
                        
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): ImprovementLayerDB = ImprovementLayerDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idImprovement = rs.getInt("id_improvement"),
                idLayer = rs.getInt("id_layer"),
                        
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
