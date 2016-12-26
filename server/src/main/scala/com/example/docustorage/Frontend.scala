package com.example.docustorage

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive
import akka.pattern._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Send}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.example.util.Configuration
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class Frontend(configuration: Configuration, system: ActorSystem)  {

  val mediator = DistributedPubSub(system).mediator

  implicit val timeout = Timeout(1 second)

  val route: Route =
    path("document" / Segment) { key =>
      get {
        val op = (mediator ? publishOperation(RetrieveMessage(key)))
        complete(op.mapTo[RetrieveReplyMessage])
      } ~ post {
        entity(as[String]) { text =>
          mediator ! publishOperation(PutMessage(key,text))
          // TODO: check absence of exceptions
          complete(HttpResponse(status=StatusCodes.OK))
        }
      } ~ delete {
        mediator ! publishOperation(RemoveMessage(key))
        complete(HttpResponse(status=StatusCodes.OK))
      }
    } ~ path("search" / Segment ) { tokens =>
      val collect: Future[SearchReplyMessage] = runCollect(tokens.split(" ").toSet)
      complete(collect)
    }


  def runCollect(tokens:Set[String]): Future[SearchReplyMessage] =
  {
    val p = Promise[SearchReplyMessage]
    val collectActor = system.actorOf(
      Props(classOf[CollectSearchRepliesActor],configuration.nodesInCluster(),configuration.nodeTimeout(),p)
    )
    mediator.tell(Publish(Topics.search,SearchMessage(tokens),sendOneMessageToEachGroup = true),collectActor)
    p.future
  }


  def publishOperation(msg:DocumentNodeMessage):Publish =
  {
    val nodeId = retrieveNodeId(msg.key)
    Publish(Topics.operations(nodeId),msg, sendOneMessageToEachGroup = true)
  }

  def retrieveNodeId(key:String):Int =
   {
     key.hashCode % configuration.nodesInCluster()
   }

}
