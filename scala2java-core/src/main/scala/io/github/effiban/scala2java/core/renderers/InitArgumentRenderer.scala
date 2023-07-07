package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, InitContext}

import scala.meta.Init

private[renderers] class InitArgumentRenderer(initRenderer: => InitRenderer,
                                              initContext: InitContext) extends ArgumentRenderer[Init] {
  override def render(init: Init, argContext: ArgumentContext): Unit = initRenderer.render(init, initContext)
}
