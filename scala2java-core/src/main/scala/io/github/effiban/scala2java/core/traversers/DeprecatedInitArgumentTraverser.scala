package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, InitContext}

import scala.meta.Init

@deprecated
private[traversers] class DeprecatedInitArgumentTraverser(initTraverser: => DeprecatedInitTraverser,
                                                          initContext: InitContext) extends DeprecatedArgumentTraverser[Init] {
  override def traverse(init: Init, argContext: ArgumentContext): Unit = initTraverser.traverse(init, initContext)
}
