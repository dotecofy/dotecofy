import com.dotecofy._
import com.dotecofy.workspace.feature._
import org.scalatra._
import javax.servlet.ServletContext
import org.slf4j.LoggerFactory
import scalikejdbc._
import scalikejdbc.config._

class ScalatraBootstrap extends LifeCycle {

   //val logger = LoggerFactory.getLogger(getClass)

	  //val cpds = new ComboPooledDataSource
	  //logger.info("Created c3p0 connection pool")

    DBs.setupAll()

	
	  override def init(context: ServletContext) {

      context.initParameters("org.scalatra.cors.allowedOrigins") = "http://localhost:8000"
      context.initParameters("org.scalatra.cors.allowCredentials") = "false"
	 	//val db = Database.forDataSource(cpds, None)   // create the Database object
	 	context.mount(new DotecofyServlet, "/*")   // create and mount the Scalatra application
	  }
	
	  private def closeDbConnection() {
	 //	logger.info("Closing c3po connection pool")
	 	//cpds.close
      DBs.closeAll()
	  }
	
	  override def destroy(context: ServletContext) {
		 super.destroy(context)
	 	closeDbConnection
	  }


  

}
