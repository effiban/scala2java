package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term

private[renderers] class TermParamArgumentRenderer(termParamRenderer: => TermParamRenderer) extends ArgumentRenderer[Term.Param] {
  override def render(termParam: Term.Param, argContext: ArgumentContext): Unit =
    termParamRenderer.render(termParam)
}
