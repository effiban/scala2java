package com.effiban.scala2java

import scala.meta.Init

object InitListTraverser {

  def traverse(inits: List[Init]): Unit = {
    if (inits.nonEmpty) {
      ArgumentListTraverser.traverse(args = inits, argTraverser = InitTraverser)
    }
  }
}
