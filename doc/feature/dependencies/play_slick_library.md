# Play Slick library

Slick library version 3.0

Slick should connect to a localhost MariaDB.

Creation of the database:

CREATE DATABASE dotecofy CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'dotecofy_user1'@'localhost' identified by 'Pa$$1337';
GRANT ALL PRIVILEGES ON dotecofy.* TO 'dotecofy_user1'@'localhost'; 

conf/application.conf :
slick.dbs.default.driver="slick.driver.MySQLDriver$"
slick.dbs.default.db.driver="org.mariadb.jdbc.Driver"
slick.dbs.default.db.url="jdbc:mariadb://localhost:3306/dotecofy"
slick.dbs.default.db.user=dotecofy_user1
slick.dbs.default.db.password="Pa$$1337"

## Layers

### Dependencies

Installation of the slick library by adding the dependencies : 

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0"
  "org.mariadb.jdbc" % "mariadb-java-client" % "2.3.0"
)

## Tests

	* Verify manually that the play framework can connect to the database

## Improvement

### impr_0.18.12.9.0001
	* Installing the slick library
	
## Verification
	* [10.12.2018, Joël] the framework runs on localhost:9000: Welcome to Play!