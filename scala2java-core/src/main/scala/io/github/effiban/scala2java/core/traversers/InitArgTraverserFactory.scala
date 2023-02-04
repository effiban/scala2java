package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InitContext

import scala.meta.Init

trait InitArgTraverserFactory {

  def apply(initContext: InitContext): ArgumentTraverser[Init]
}

class InitArgTraverserFactoryImpl(initTraverser: => InitTraverser) extends InitArgTraverserFactory {

  override def apply(initContext: InitContext): ArgumentTraverser[Init] = new InitArgumentTraverser(initTraverser, initContext)
}
