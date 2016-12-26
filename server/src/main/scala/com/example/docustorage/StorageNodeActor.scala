package com.example.docustorage

import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import com.example.util.Configuration

class StorageNodeActor(nodeStorage: NodeStorage, nodeId: Int, configuration: Configuration) extends Actor
{

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe(Topics.operations(nodeId),Some(nodeId.toString),self)


  override def receive = {
    case PutMessage(key,document) =>
       nodeStorage.put(key,document)
    case RetrieveMessage(key) =>
       sender ! RetrieveReplyMessage(key,nodeStorage.get(key))
    case RemoveMessage(key) =>
        nodeStorage.remove(key)
  }

}
