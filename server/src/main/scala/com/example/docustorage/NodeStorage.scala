package com.example.docustorage

import java.util.concurrent.ConcurrentHashMap
import java.util.{Iterator=>JIterator}

/**
  * One instance on each storage node.
  * Can be called from different actors simulanteously.
  * In real life will be interface to read db.
  */
class NodeStorage {

  private[this] val storage = new ConcurrentHashMap[String, String]()
  private[this] val revIndex = new ConcurrentHashMap[String, ConcurrentHashMap[String, Boolean]]()

  def put(key: String, document: String): Unit = {
    storage.put(key, document)
    val tokens: Set[String] = retrieveTokens(document)
    for (token <- tokens) {
      val documentsWithToken = Option(revIndex.get(token)) match {
        case None => val c = new ConcurrentHashMap[String, Boolean]()
          revIndex.put(token, c)
          c
        case Some(c) => c
      }
      documentsWithToken.put(key, true)
    }
  }

  def get(key: String): Option[String] =
    Option(storage.get(key))

  def searchAnd(tokens: Set[String]): Set[String] = {
    case class State(
                      notFound: Boolean = false,
                      keys: Set[String] = Set()
                    )
    val s = tokens.foldLeft(State()) { (s, e) =>
      if (s.notFound) s
      else {
        Option(revIndex.get(e)) match {
          case None => s.copy(notFound = true, keys=Set())
          case Some(ds) => val tokenKeys = enToSet(ds.keys)
                  val newKeys = tokenKeys intersect s.keys
                  val notFound = newKeys.isEmpty
                  State(notFound=notFound, keys = newKeys)
        }
      }
    }
    s.keys
  }

  def remove(key:String):Unit = {
    Option(storage.remove(key)) foreach { document =>
      val tokens = retrieveTokens(document)
      for(token <- tokens) {
        Option(revIndex.get(token)).foreach{ keys =>
           keys.remove(key)
        }
      }
    }
  }

  def enToSet(en: java.util.Enumeration[String]): Set[String] =
  {
    var retval: Set[String] = Set()
    while(en.hasMoreElements) {
      retval += en.nextElement()
    }
    retval
  }

  def retrieveTokens(document:String):Set[String]=
  {
    val tokenCandidates = document.split(" ")
    (for {tc <- tokenCandidates
         c = tc.trim if !StopWords.isA(c)
     } yield c).toSet
  }



}
