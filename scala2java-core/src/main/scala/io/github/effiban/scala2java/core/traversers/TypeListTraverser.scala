package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.renderers.{ArgumentListRenderer, ArgumentRenderer}

import scala.meta.Type

trait TypeListTraverser {
  def traverse(types: List[Type]): Unit
}

private[traversers] class TypeListTraverserImpl(argumentListRenderer: => ArgumentListRenderer,
                                                typeArgRenderer: => ArgumentRenderer[Type]) extends TypeListTraverser {

  override def traverse(types: List[Type]): Unit = {
    argumentListRenderer.render(args = types,
      // TODO - call the renderer with an argument indicating that Java primitives should be boxed
      argRenderer = typeArgRenderer,
      context = ArgumentListContext(options = ListTraversalOptions(maybeEnclosingDelimiter = Some(AngleBracket), onSameLine = true))
    )
  }
}
