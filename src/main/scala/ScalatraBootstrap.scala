
import com.dotecofy.access.group.GroupAPI
import com.dotecofy.access.right.RightAPI
import com.dotecofy.access.user.UserAPI
import com.dotecofy.context.`type`.TypeAPI
import com.dotecofy.context.cycle.CycleAPI
import com.dotecofy.context.layer.LayerAPI
import com.dotecofy.improvement.assignment.AssignmentAPI
import com.dotecofy.improvement.improvement.ImprovementAPI
import com.dotecofy.improvement.output.OutputAPI
import com.dotecofy.improvement.verification.VerificationAPI
import com.dotecofy.workspace.feature._
import com.dotecofy.workspace.project.ProjectAPI
import com.dotecofy.workspace.version.VersionAPI
import com.dotecofy.workspace.workspace.WorkspaceAPI
import javax.servlet.ServletContext
import org.scalatra._
import scalikejdbc.config.DBs


class ScalatraBootstrap extends LifeCycle {

  //val logger = LoggerFactory.getLogger(getClass)

  //val cpds = new ComboPooledDataSource
  //logger.info("Created c3p0 connection pool")

  DBs.setupAll()


  override def init(context: ServletContext) {

    context.initParameters("org.scalatra.cors.allowedOrigins") = "http://localhost:8000"
    context.initParameters("org.scalatra.cors.allowCredentials") = "false"
    //val db = Database.forDataSource(cpds, None)   // create the Database object
    //context.mount(new DotecofyServlet, "/*")   // create and mount the Scalatra application

    context.mount(new ProjectAPI, "/projects")
    context.mount(new FeatureAPI, "/features")
    context.mount(new WorkspaceAPI, "/workspaces")
    context.mount(new VersionAPI, "/versions")

    context.mount(new ImprovementAPI, "/improvements")
    context.mount(new AssignmentAPI, "/assignments")
    context.mount(new OutputAPI, "/outputs")
    context.mount(new VerificationAPI, "/verifications")

    context.mount(new CycleAPI, "/cycles")
    context.mount(new LayerAPI, "/layers")
    context.mount(new TypeAPI, "/types")

    context.mount(new UserAPI, "/users")
    context.mount(new GroupAPI, "/groups")
    context.mount(new RightAPI, "/rights")
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection
  }

  private def closeDbConnection() {
    //	logger.info("Closing c3po connection pool")
    //cpds.close
    DBs.closeAll()
  }


}
