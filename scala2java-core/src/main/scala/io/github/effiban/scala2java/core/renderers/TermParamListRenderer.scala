package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, TermParamListRenderContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Term

trait TermParamListRenderer {
  def render(termParams: List[Term.Param], context: TermParamListRenderContext): Unit
}

private[renderers] class TermParamListRendererImpl(argumentListRenderer: => ArgumentListRenderer,
                                                   termParamArgRendererFactory: => TermParamArgRendererFactory) extends TermParamListRenderer {

  override def render(termParams: List[Term.Param], context: TermParamListRenderContext): Unit = {
    val options = ListTraversalOptions(
      onSameLine = context.onSameLine,
      maybeEnclosingDelimiter = Some(Parentheses),
      traverseEmpty = true
    )

    argumentListRenderer.render(
      args = termParams,
      argRendererProvider = (idx: Int) => termParamArgRendererFactory.create(context, idx),
      context = ArgumentListContext(options = options)
    )
  }
}
