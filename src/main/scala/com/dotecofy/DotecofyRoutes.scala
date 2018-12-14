package com.dotecofy

import com.dotecofy.improvement.improvement.ImprovementServices
import com.dotecofy.workspace.feature._
import org.scalatra.{FutureSupport, ScalatraBase, ScalatraServlet}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatra.CorsSupport

trait DotecofyRoutes extends ScalatraBase with FutureSupport with JacksonJsonSupport  with CorsSupport {

  // def db: Database

  /*get("/db/create-tables") {
    db.run(Tables.createSchemaAction)
  }

  get("/db/load-data") {
    db.run(Tables.insertSupplierAndCoffeeData)
  }

  get("/db/drop-tables") {
    db.run(Tables.dropSchemaAction)
  }

  get("/coffees") {
    // run the action and map the result to something more readable
    db.run(Tables.findCoffeesWithSuppliers.result) map { xs =>
      contentType = "text/plain"
      xs map { case (s1, s2) => f"  $s1 supplied by $s2" } mkString "\n"
    }
  }*/
  options("/*") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
  }

  get("/") {
    //views.html.hello()
  }

  get("/db/create-tables") {
    //db.run(Tables.createSchemaAction)
  }

  get("/features") {
    FeatureServices.load(0, 50)
  }

  get("/features/page/:nb") {
    //featureComponent.featureServices.load(0,50)
  }

  get("/versions") {
    VersionServices.load(0, 50)
  }

  get("/improvements") {
    ImprovementServices.load(0, 50)
  }

  /*get("/features") {
      // run the action and map the result to something more readable
      db.run(Tables.findCoffeesWithSuppliers.result) map { xs =>
        contentType = "text/plain"
        xs map { case (s1, s2) => f"  $s1 supplied by $s2" } mkString "\n"
      }
    }*/

}