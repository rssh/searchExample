package com.example.docustorage

sealed trait DocumentMessage

sealed trait DocumentNodeMessage extends DocumentMessage
{ def key: String }

sealed trait DocumentAllNodesMessage extends DocumentMessage
sealed trait DocumentReplyMessage extends DocumentMessage

case class PutMessage(key:String,document:String) extends DocumentNodeMessage

case class RetrieveMessage(key:String) extends DocumentNodeMessage

case class RetrieveReplyMessage(key:String, document: Option[String]) extends DocumentReplyMessage

object RetrieveReplyMessage extends ((String, Option[String]) => RetrieveReplyMessage)
{
  import spray.json.DefaultJsonProtocol._
  implicit val jsonFormat = jsonFormat2(RetrieveReplyMessage)
}


case class RemoveMessage(key:String) extends DocumentNodeMessage

case class SearchMessage(tokens:Set[String]) extends DocumentAllNodesMessage

case class SearchReplyMessage(keys:Set[String]) extends DocumentReplyMessage

object SearchReplyMessage extends (Set[String] => SearchReplyMessage)
{

  import spray.json.DefaultJsonProtocol._
  implicit val jsonFormat = jsonFormat1(SearchReplyMessage)

}


object DocumentNodeMessage
{
  object Key {
    def unapply(x: Any): Option[String] =
      x match {
        case xx: DocumentNodeMessage => Some(xx.key)
        case _ => None
      }
  }

}

