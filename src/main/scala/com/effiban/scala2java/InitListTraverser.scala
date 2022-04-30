package com.effiban.scala2java

import scala.meta.Init

trait InitListTraverser {
  def traverse(inits: List[Init]): Unit
}

private[scala2java] class InitListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                initTraverser: => InitTraverser) extends InitListTraverser {

  override def traverse(inits: List[Init]): Unit = {
    if (inits.nonEmpty) {
      argumentListTraverser.traverse(args = inits, argTraverser = initTraverser)
    }
  }
}

object InitListTraverser extends InitListTraverserImpl(ArgumentListTraverser, InitTraverser)
