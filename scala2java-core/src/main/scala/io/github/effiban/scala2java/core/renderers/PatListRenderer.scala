package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Pat

trait PatListRenderer {
  def render(pats: List[Pat]): Unit
}

private[renderers] class PatListRendererImpl(argumentListRenderer: => ArgumentListRenderer,
                                             patArgRenderer: => ArgumentRenderer[Pat]) extends PatListRenderer {

  override def render(pats: List[Pat]): Unit = {
    argumentListRenderer.render(
      args = pats,
      argRenderer = patArgRenderer,
      context = ArgumentListContext(options = ListTraversalOptions(onSameLine = true))
    )
  }
}
