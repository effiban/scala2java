package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Type

trait TypeListRenderer {
  def render(types: List[Type]): Unit
}

private[renderers] class TypeListRendererImpl(argumentListRenderer: => ArgumentListRenderer,
                                              typeArgRenderer: => ArgumentRenderer[Type]) extends TypeListRenderer {

  override def render(types: List[Type]): Unit = {
    argumentListRenderer.render(args = types,
      // TODO - call the renderer with an argument indicating that Java primitives should be boxed
      argRendererProvider = _ => typeArgRenderer,
      context = ArgumentListContext(options = ListTraversalOptions(maybeEnclosingDelimiter = Some(AngleBracket), onSameLine = true))
    )
  }
}
