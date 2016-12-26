package launcher

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.example.docustorage.{Frontend, NodeStorage, SearchNodeActor, StorageNodeActor}
import com.example.util.Configuration

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ClusterMain {

  implicit val system = ActorSystem()

  def main(args: Array[String]):Unit =
  {
    val nodeId = retrieveNode(args)

    val configuration = new Configuration()

    val frontend = new Frontend(configuration,system)

    val nodeStorage = new NodeStorage()
    for(i <- 1 to configuration.workersInNode() ) {
      val storageWorker = system.actorOf(Props(classOf[StorageNodeActor],nodeStorage,nodeId,configuration))
      val searchNode = system.actorOf(Props(classOf[SearchNodeActor],nodeStorage,nodeId,configuration))
    }


    implicit val am = ActorMaterializer()


    val bindingFuture = Http().bindAndHandle(frontend.route,configuration.getHost(nodeId),configuration.getPort(nodeId))

    Await.ready(bindingFuture,Duration.Inf)

  }

  // retrieve node or throw exception
  def retrieveNode(args:Array[String]):Int =
  {
    var i=0
    var node: Option[Int] = None
    while(i < args.length && !node.isDefined) {
      if (args(i)=="--node") {
        i += 1
        node = Some(args(i).toInt)
      }
      i += 1
    }
    node.getOrElse(throw new IllegalStateException("progeam is started without --node parameter"))
  }

}
