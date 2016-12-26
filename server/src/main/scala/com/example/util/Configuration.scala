package com.example.util

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

/**
  * Wrapper arround configiuration
  */
class Configuration {

  def nodesInCluster(): Int =
    Try(config.getInt("docusearch.nodes-in-cluster")).getOrElse(2)

  def nodeTimeout(): Duration =
    Try(config.getLong("docusearch.node-timeout")).getOrElse(2000L) millisecond


  def workersInNode(): Int =
    Try(config.getInt("docusearch.workers-in-node")).getOrElse(1)

  def getHost(nodeId:Int):String =
    Try(config.getString(s"docusearch.nodes.${nodeId}.host")).getOrElse("127.0.0.1")

  def getPort(nodeId:Int):Int =
    Try(config.getInt(s"docusearch.nodes.${nodeId}.port")).getOrElse(8080+nodeId)


  // can be overloaded in subtypes.
  lazy val config = ConfigFactory.load()



}
