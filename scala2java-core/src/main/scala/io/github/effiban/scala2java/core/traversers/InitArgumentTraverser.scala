package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, InitContext}

import scala.meta.Init

private[traversers] class InitArgumentTraverser(initTraverser: => InitTraverser,
                                                initContext: InitContext) extends ArgumentTraverser[Init] {
  override def traverse(init: Init, argContext: ArgumentContext): Unit = initTraverser.traverse(init, initContext)
}
