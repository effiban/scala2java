package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InitContext

import scala.meta.Init

trait InitListTraverser {
  def traverse(inits: List[Init], context: InitContext = InitContext()): Unit
}

private[traversers] class InitListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                initArgTraverserFactory: => InitArgTraverserFactory) extends InitListTraverser {

  override def traverse(inits: List[Init], context: InitContext = InitContext()): Unit = {
    argumentListTraverser.traverse(
      args = inits,
      argTraverser = initArgTraverserFactory(context)
    )
  }
}
