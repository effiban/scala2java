package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.InitContext

import scala.meta.Init

trait InitArgRendererFactory {

  def apply(initContext: InitContext): ArgumentRenderer[Init]
}

class InitArgRendererFactoryImpl(initRenderer: => InitRenderer) extends InitArgRendererFactory {

  override def apply(initContext: InitContext): ArgumentRenderer[Init] = new InitArgumentRenderer(initRenderer, initContext)
}
