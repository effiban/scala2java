package com.effiban.scala2java

import scala.meta.Init

trait InitListTraverser {
  def traverse(inits: List[Init]): Unit
}

object InitListTraverser extends InitListTraverser {

  override def traverse(inits: List[Init]): Unit = {
    if (inits.nonEmpty) {
      ArgumentListTraverser.traverse(args = inits, argTraverser = InitTraverser)
    }
  }
}
