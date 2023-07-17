package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.renderers.contexts.InitRenderContext

import scala.meta.Init

private[renderers] class InitArgumentRenderer(initRenderer: => InitRenderer,
                                              initContext: InitRenderContext) extends ArgumentRenderer[Init] {
  override def render(init: Init, argContext: ArgumentContext): Unit = initRenderer.render(init, initContext)
}
