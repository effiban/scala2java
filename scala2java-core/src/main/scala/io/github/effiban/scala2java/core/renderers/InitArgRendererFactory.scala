package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.InitRenderContext

import scala.meta.Init

trait InitArgRendererFactory {

  def apply(initContext: InitRenderContext): ArgumentRenderer[Init]
}

class InitArgRendererFactoryImpl(initRenderer: => InitRenderer) extends InitArgRendererFactory {

  override def apply(initContext: InitRenderContext): ArgumentRenderer[Init] = new InitArgumentRenderer(initRenderer, initContext)
}
