package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Type

trait TypeListTraverser {
  def traverse(types: List[Type]): Unit
}

private[traversers] class TypeListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                typeArgTraverser: => ArgumentTraverser[Type]) extends TypeListTraverser {

  override def traverse(types: List[Type]): Unit = {
    argumentListTraverser.traverse(args = types,
      // TODO - call the traverser with an argument indicating that Java primitives should be boxed
      argTraverser = typeArgTraverser,
      context = ArgumentListContext(options = ListTraversalOptions(maybeEnclosingDelimiter = Some(AngleBracket), onSameLine = true))
    )
  }
}
