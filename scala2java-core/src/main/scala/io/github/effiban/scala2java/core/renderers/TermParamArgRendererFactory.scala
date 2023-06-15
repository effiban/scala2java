package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{TermParamListRenderContext, TermParamRenderContext}

import scala.meta.Term

trait TermParamArgRendererFactory extends (TermParamListRenderContext => ArgumentRenderer[Term.Param])

class TermParamArgRendererFactoryImpl(termParamRenderer: => TermParamRenderer) extends TermParamArgRendererFactory {

  override def apply(paramListContext: TermParamListRenderContext): ArgumentRenderer[Term.Param] = {
    new TermParamArgumentRenderer(termParamRenderer, TermParamRenderContext(paramListContext.javaModifiers))
  }
}