val ScalatraVersion = "2.6.4"

organization := "com.dotecofy"

name := "dotecofy"

version := "0.18.12.9"

scalaVersion := "2.12.8"

resolvers += Classpaths.typesafeReleases


libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
)


libraryDependencies ++= Seq(
  //"com.typesafe.slick" %% "slick" % "3.2.0",
  "com.zaxxer" % "HikariCP" % "3.2.0",
  "org.mariadb.jdbc" % "mariadb-java-client" % "2.3.0",
)


libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.json4s" %% "json4s-jackson" % "3.5.2",
  "com.pauldijou" %% "jwt-json4s-jackson" % "1.0.0"
)

//libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "4.3.4" % "test",
  "org.scalatra" %% "scalatra-specs2" % "2.6.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
  "com.github.agourlay" %% "cornichon-test-framework" % "0.16.3" % Test,
  "org.specs2" %% "specs2-matcher-extra" % "4.3.6" % Test,

  //"org.specs2" %% "specs2-junit" % "4.3.6" % Test,
  //"org.junit.jupiter" % "junit-jupiter-engine" % "5.3.2" % Test
)

libraryDependencies += "com.github.slugify" % "slugify" % "2.3"

libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.9.0"



scalacOptions in Test ++= Seq("-Yrangepos")

testFrameworks += new TestFramework("com.github.agourlay.cornichon.framework.CornichonFramework")
//enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)

lazy val sbf = Project(id = "cloud-dest-sbf",
  base = file("../cloud.dest.sbf"))