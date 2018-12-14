val ScalatraVersion = "2.6.4"

organization := "com.dotecofy"

name := "dotecofy"

version := "0.18.12.9"

scalaVersion := "2.12.8"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
)


libraryDependencies ++= Seq(
  //"com.typesafe.slick" %% "slick" % "3.2.0",
  "org.scalikejdbc" %% "scalikejdbc"        % "3.3.1",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "3.3.1",
  "org.scalikejdbc" %% "scalikejdbc-test"   % "3.3.1"   % "test",
  "org.mariadb.jdbc" % "mariadb-java-client" % "2.3.0",
)


libraryDependencies ++= Seq(
	"org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.json4s"   %% "json4s-jackson" % "3.5.2"
)




enablePlugins(ScalikejdbcPlugin)

//enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
