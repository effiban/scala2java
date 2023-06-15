package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, TermParamRenderContext}

import scala.meta.Term

private[renderers] class TermParamArgumentRenderer(termParamRenderer: => TermParamRenderer,
                                                   val termParamRenderContext: TermParamRenderContext) extends ArgumentRenderer[Term.Param] {
  override def render(termParam: Term.Param, argContext: ArgumentContext): Unit =
    termParamRenderer.render(termParam, termParamRenderContext)
}
