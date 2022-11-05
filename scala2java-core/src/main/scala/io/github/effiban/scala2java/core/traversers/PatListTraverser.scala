package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Pat

trait PatListTraverser {
  def traverse(pats: List[Pat]): Unit
}

private[traversers] class PatListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                               patTraverser: => PatTraverser) extends PatListTraverser {

  override def traverse(pats: List[Pat]): Unit = {
      argumentListTraverser.traverse(args = pats,
        argTraverser = patTraverser,
        ListTraversalOptions(onSameLine = true))
  }
}
