package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.TermParamListRenderContext

import scala.meta.Term

trait TermParamArgRendererFactory {

  def create(paramListContext: TermParamListRenderContext, idx: Int): ArgumentRenderer[Term.Param]
}

class TermParamArgRendererFactoryImpl(termParamRenderer: => TermParamRenderer) extends TermParamArgRendererFactory {

  override def create(paramListContext: TermParamListRenderContext, idx: Int): ArgumentRenderer[Term.Param] = {
    idx match {
      case anIdx if idx >= 0 || idx < paramListContext.paramContexts.length =>
        new TermParamArgumentRenderer(termParamRenderer, paramListContext.paramContexts(anIdx))
      case anIdx => throw new IllegalStateException(
        s"Cannot create TermParamRenderContext because param index $anIdx is out-of-bounds " +
          s"(numParams=${paramListContext.paramContexts.length})"
      )
    }
  }
}
