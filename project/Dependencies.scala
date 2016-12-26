import sbt._

object Dependencies {
  val akkaVersion = "2.4.16"
  val akkaHttpVersion = "10.0.1"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.0"
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  lazy val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % akkaVersion
  lazy val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
  lazy val akkaClusterTools = "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion
  lazy val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  lazy val akkaHttpSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
}
