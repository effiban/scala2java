package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.renderers.contexts.TermParamListRenderContext

import scala.meta.Term

trait TermParamListRenderer {
  def render(termParams: List[Term.Param], context: TermParamListRenderContext = TermParamListRenderContext()): Unit
}

private[renderers] class TermParamListRendererImpl(argumentListRenderer: => ArgumentListRenderer,
                                                   termParamArgumentRenderer: => ArgumentRenderer[Term.Param])
  extends TermParamListRenderer {

  override def render(termParams: List[Term.Param], context: TermParamListRenderContext = TermParamListRenderContext()): Unit = {
    val options = ListTraversalOptions(
      onSameLine = context.onSameLine,
      maybeEnclosingDelimiter = Some(Parentheses),
      traverseEmpty = true
    )

    argumentListRenderer.render(
      args = termParams,
      argRenderer = termParamArgumentRenderer,
      context = ArgumentListContext(options = options)
    )
  }
}