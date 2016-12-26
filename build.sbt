import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.example",
  scalaVersion := "2.12.1",
  scalacOptions ++= Seq("-unchecked","-deprecation", "-feature"
                         /*,  "-Ydebug"  */
                     ),
  libraryDependencies += scalaTest % Test,
  libraryDependencies += akkaActor
)


lazy val server  = (project in file("server")).
  settings(commonSettings: _*).
  settings(
    name := "Server",
    libraryDependencies ++= Seq(akkaCluster, akkaClusterTools, akkaHttp, akkaHttpSprayJson)
  )

lazy val client  = (project in file("client")).
  settings(commonSettings: _*).
  settings(
    name := "Server"
  )


lazy val root = (project in file(".")).
  aggregate(client,server)

