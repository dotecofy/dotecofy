

package com.dotecofy.improvement.improvementcycle

import java.sql.{SQLException, SQLIntegrityConstraintViolationException}
import java.time.ZonedDateTime

import cloud.dest.sbf.exception.{CommonError, Error, ErrorBuilder}
import cloud.dest.sbf.tools.DateConverter

import java.sql._

import scala.collection.mutable.ListBuffer

case class ImprovementCycleDB(

    id:Int = 0,

    signature:String = null,
     idImprovement: Int = 0,
         idCycle: Int = 0,
    
    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

case class ImprovementCycleSelect(

    signature:String = null,
     improvementName: String = null,
     improvementSig: String = null,
     cycleName: String = null,
     cycleSig: String = null,

    createdDate: ZonedDateTime = null,

    updatedDate: Option[ZonedDateTime] = None,
)

trait ImprovementCycleRepositoryComponent {

def create(improvementcycle: ImprovementCycleDB)(implicit conn: Connection): Either[Error, ImprovementCycleSelect]

def update(id: Int, improvementcycle: ImprovementCycleDB)(implicit conn: Connection): Either[Error, ImprovementCycleSelect]

def delete(id: Int)(implicit conn: Connection): Either[Error, Unit]

def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementCycleSelect]]

def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementCycleDB]]


def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementCycleSelect]]

def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementCycleSelect]]
def findByCycle(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementCycleSelect]]


}

abstract class ImprovementCycleRepository extends ImprovementCycleRepositoryComponent {

    override def create(improvementcycle: ImprovementCycleDB)(implicit conn: Connection): Either[Error, ImprovementCycleSelect] = {
        try {
            val stmt = conn.prepareStatement("INSERT INTO improvement_cycle(signature,id_improvement,id_cycle,created_date) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)

            stmt.setString(1,improvementcycle.signature)
stmt.setInt(2,improvementcycle.idImprovement)
stmt.setInt(3,improvementcycle.idCycle)
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

    override def update(id: Int, improvementcycle: ImprovementCycleDB)(implicit conn: Connection): Either[Error, ImprovementCycleSelect] = {

        try {
            val stmt = conn.prepareStatement("UPDATE improvement_cycle SET signature=?,updated_date=? WHERE id=?")

            stmt.setString(1,improvementcycle.signature)
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
            val stmt = conn.prepareStatement("DELETE FROM improvement_cycle WHERE improvement_cycle.id=?")
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

    override def findByProfile(profileId: Int, index: Int, nb: Int, col:String=null, value:String=null)(implicit conn: Connection): Either[Error, List[ImprovementCycleSelect]] = {

        try {
            var stmt:PreparedStatement = null
            if(col == null || value == null || col.isEmpty || value.isEmpty)
                stmt = conn.prepareStatement("SELECT improvement_cycle.*,improvement.name as improvementName,improvement.signature as improvementSig,cycle.name as cycleName,cycle.signature as cycleSig FROM improvement_cycle INNER JOIN improvement ON improvement_cycle.id_improvement=improvement.id INNER JOIN cycle ON improvement_cycle.id_cycle=cycle.id")
            else col match {
                case "signature" => {
                    stmt = conn.prepareStatement("SELECT improvement_cycle.*,improvement.name as improvementName,improvement.signature as improvementSig,cycle.name as cycleName,cycle.signature as cycleSig FROM improvement_cycle INNER JOIN improvement ON improvement_cycle.id_improvement=improvement.id INNER JOIN cycle ON improvement_cycle.id_cycle=cycle.id WHERE improvement_cycle.signature=?")
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

    private def find(id: Int)(implicit conn: Connection): Option[ImprovementCycleSelect] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_cycle.*,improvement.name as improvementName,improvement.signature as improvementSig,cycle.name as cycleName,cycle.signature as cycleSig FROM improvement_cycle INNER JOIN improvement ON improvement_cycle.id_improvement=improvement.id INNER JOIN cycle ON improvement_cycle.id_cycle=cycle.id WHERE improvement_cycle.id=?")
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

    
    override def findBySignature(signature: String)(implicit conn: Connection): Either[Error, Option[ImprovementCycleSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_cycle.*,improvement.name as improvementName,improvement.signature as improvementSig,cycle.name as cycleName,cycle.signature as cycleSig FROM improvement_cycle INNER JOIN improvement ON improvement_cycle.id_improvement=improvement.id INNER JOIN cycle ON improvement_cycle.id_cycle=cycle.id WHERE improvement_cycle.signature=?")
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
       
        override def findByImprovement(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementCycleSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_cycle.*,improvement.name as improvementName,improvement.signature as improvementSig,cycle.name as cycleName,cycle.signature as cycleSig FROM improvement_cycle INNER JOIN improvement ON improvement_cycle.id_improvement=improvement.id INNER JOIN cycle ON improvement_cycle.id_cycle=cycle.id WHERE improvement.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
        override def findByCycle(signature: String)(implicit conn: Connection): Either[Error, List[ImprovementCycleSelect]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_cycle.*,improvement.name as improvementName,improvement.signature as improvementSig,cycle.name as cycleName,cycle.signature as cycleSig FROM improvement_cycle INNER JOIN improvement ON improvement_cycle.id_improvement=improvement.id INNER JOIN cycle ON improvement_cycle.id_cycle=cycle.id WHERE cycle.signature=?")
            stmt.setString(1, signature)
            val rs:ResultSet = stmt.executeQuery

            Right(applyList(rs))
        } catch {
            case sql: SQLException => Left(Error(ErrorBuilder.SQL_EXCEPTION, sql.getErrorCode.toString, Option(sql.getMessage), Option(List())))
        } finally {
            conn.close()
        }
    }
    
    
    override def findIfAllowed(profileId: Int, signature:String)(implicit conn: Connection): Either[Error, Option[ImprovementCycleDB]] = {
        try {
            val stmt = conn.prepareStatement("SELECT improvement_cycle.*,improvement.name as improvementName,improvement.signature as improvementSig,cycle.name as cycleName,cycle.signature as cycleSig FROM improvement_cycle INNER JOIN improvement ON improvement_cycle.id_improvement=improvement.id INNER JOIN cycle ON improvement_cycle.id_cycle=cycle.id WHERE improvement_cycle.signature=?")
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

    private def applyList(rs: ResultSet): List[ImprovementCycleSelect] = {
        val res : ListBuffer[ImprovementCycleSelect] = ListBuffer.empty[ImprovementCycleSelect]
        while(rs.next) {
            res+=apply(rs)
        }
        res.toList
    }

    private def apply(rs: ResultSet): ImprovementCycleSelect = ImprovementCycleSelect(
        
        signature = rs.getString("signature"),
                        improvementName = rs.getString("improvementName"),
        improvementSig = rs.getString("improvementSig"),
                cycleName = rs.getString("cycleName"),
        cycleSig = rs.getString("cycleSig"),
                        
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )

    private def applyDB(rs: ResultSet): ImprovementCycleDB = ImprovementCycleDB(
        id = rs.getInt("id"),
        
        signature = rs.getString("signature"),
                        idImprovement = rs.getInt("id_improvement"),
                idCycle = rs.getInt("id_cycle"),
                        
        createdDate = DateConverter.toZonedDateTime(rs.getTimestamp("created_date")).orNull,        
        updatedDate = DateConverter.toZonedDateTime(rs.getTimestamp("updated_date"))    )
}
