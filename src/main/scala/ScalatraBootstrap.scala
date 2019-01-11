
import cloud.dest.sbf.auth.server.AuthServerAPI
import com.dotecofy.access.group.GroupAPISub
import com.dotecofy.access.right.RightAPI
import com.dotecofy.access.user.UserAPISub
import com.dotecofy.context.cycle.CycleAPISub
import com.dotecofy.context.kind.KindAPISub
import com.dotecofy.context.layer.LayerAPISub
import com.dotecofy.improvement.assignment.AssignmentAPISub
import com.dotecofy.improvement.improvement.ImprovementAPISub
import com.dotecofy.improvement.output.OutputAPISub
import com.dotecofy.improvement.verification.VerificationAPI
import com.dotecofy.workspace.feature._
import com.dotecofy.workspace.project.ProjectAPISub
import com.dotecofy.workspace.version.VersionAPISub
import com.dotecofy.workspace.workspace.WorkspaceAPISub
import javax.servlet.ServletContext
import org.scalatra._


class ScalatraBootstrap extends LifeCycle {

  //val logger = LoggerFactory.getLogger(getClass)

  //val cpds = new ComboPooledDataSource
  //logger.info("Created c3p0 connection pool")


  override def init(context: ServletContext) {

    context.initParameters("org.scalatra.cors.allowedOrigins") = "http://localhost:8000"
    context.initParameters("org.scalatra.cors.allowCredentials") = "false"
    //val db = Database.forDataSource(cpds, None)   // create the Database object
    //context.mount(new DotecofyServlet, "/*")   // create and mount the Scalatra application

    context.mount(new AuthServerAPI, "/auth")

    context.mount(new ProjectAPISub, "/projects")
    context.mount(new FeatureAPISub, "/features")
    context.mount(new WorkspaceAPISub, "/workspaces")
    context.mount(new VersionAPISub, "/versions")

    context.mount(new ImprovementAPISub, "/improvements")
    context.mount(new AssignmentAPISub, "/assignments")
    context.mount(new OutputAPISub, "/outputs")
    context.mount(new VerificationAPI, "/verifications")

    context.mount(new CycleAPISub, "/cycles")
    context.mount(new LayerAPISub, "/layers")
    context.mount(new KindAPISub, "/kinds")

    context.mount(new UserAPISub, "/users")
    context.mount(new GroupAPISub, "/groups")
    context.mount(new RightAPI, "/rights")
  }

}
