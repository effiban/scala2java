package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Pat

trait PatListTraverser {
  def traverse(pats: List[Pat]): Unit
}

private[traversers] class PatListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                               patArgTraverser: => ArgumentTraverser[Pat]) extends PatListTraverser {

  override def traverse(pats: List[Pat]): Unit = {
      argumentListTraverser.traverse(
        args = pats,
        argTraverser = patArgTraverser,
        context = ArgumentListContext(options = ListTraversalOptions(onSameLine = true))
      )
  }
}
