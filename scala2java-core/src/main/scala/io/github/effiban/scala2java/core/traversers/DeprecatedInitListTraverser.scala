package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InitContext

import scala.meta.Init

@deprecated
trait DeprecatedInitListTraverser {
  def traverse(inits: List[Init], context: InitContext = InitContext()): Unit
}

@deprecated
private[traversers] class DeprecatedInitListTraverserImpl(argumentListTraverser: => DeprecatedArgumentListTraverser,
                                                          initArgTraverserFactory: => DeprecatedInitArgTraverserFactory) extends DeprecatedInitListTraverser {

  override def traverse(inits: List[Init], context: InitContext = InitContext()): Unit = {
    argumentListTraverser.traverse(
      args = inits,
      argTraverser = initArgTraverserFactory(context)
    )
  }
}
