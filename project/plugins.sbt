addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.13")
addSbtPlugin("org.scalatra.sbt" % "sbt-scalatra" % "1.0.2")
addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "3.3.1")

libraryDependencies += "org.mariadb.jdbc" % "mariadb-java-client" % "2.3.0"
