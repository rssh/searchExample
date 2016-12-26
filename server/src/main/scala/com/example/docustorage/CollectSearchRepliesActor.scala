package com.example.docustorage

import akka.actor.{Actor, ActorRef, ReceiveTimeout}
import akka.http.scaladsl.server.Route

import scala.concurrent.Promise
import scala.concurrent.duration.Duration

class CollectSearchRepliesActor(nNodes:Int, timeout: Duration, resultPromise: Promise[SearchReplyMessage]) extends Actor {

  var mergedReplies = SearchReplyMessage(Set())
  var nReceivedReplies = 0
  context.setReceiveTimeout(timeout)

  override def receive = {
    case SearchReplyMessage(nodeKeys) =>
         mergedReplies = mergedReplies.copy(keys = mergedReplies.keys union nodeKeys )
         nReceivedReplies += 1
         if (nReceivedReplies >= nNodes) {
            resultPromise.trySuccess(mergedReplies)
            context.stop(self)
         }
    case ReceiveTimeout =>
         resultPromise.trySuccess(mergedReplies)
         context.stop(self)
  }

}


