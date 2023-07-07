package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Type

trait TypeParamListRenderer {
  def render(typeParams: List[Type.Param]): Unit
}

private[renderers] class TypeParamListRendererImpl(argumentListRenderer: => ArgumentListRenderer,
                                                   typeParamArgRenderer: => ArgumentRenderer[Type.Param]) extends TypeParamListRenderer {

  override def render(typeParams: List[Type.Param]): Unit = {
    argumentListRenderer.render(args = typeParams,
      argRenderer = typeParamArgRenderer,
      context = ArgumentListContext(options = ListTraversalOptions(maybeEnclosingDelimiter = Some(AngleBracket), onSameLine = true)))
  }
}
