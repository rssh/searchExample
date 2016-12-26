package com.example.docustorage

import akka.actor.Actor
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import com.example.util.Configuration

class SearchNodeActor(nodeStorage: NodeStorage, nodeId: Int, configuration: Configuration) extends Actor {

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(Topics.search,Some(nodeId.toString),self)

  override def receive = {
    case SearchMessage(tokens) =>
      sender ! SearchReplyMessage(nodeStorage.searchAnd(tokens))
  }


}
