package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.InitRenderContext

import scala.meta.Init

trait InitListRenderer {
  def render(inits: List[Init], context: InitRenderContext = InitRenderContext()): Unit
}

private[renderers] class InitListRendererImpl(argumentListRenderer: => ArgumentListRenderer,
                                              initArgRendererFactory: => InitArgRendererFactory) extends InitListRenderer {

  override def render(inits: List[Init], context: InitRenderContext = InitRenderContext()): Unit = {
    argumentListRenderer.render(
      args = inits,
      argRenderer = initArgRendererFactory(context)
    )
  }
}
