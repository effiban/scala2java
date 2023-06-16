package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Type

trait TypeParamListTraverser {
  def traverse(typeParams: List[Type.Param]): Unit
}

private[traversers] class TypeParamListTraverserImpl(argumentListTraverser: => DeprecatedArgumentListTraverser,
                                                     typeParamArgTraverser: => DeprecatedArgumentTraverser[Type.Param]) extends TypeParamListTraverser {

  override def traverse(typeParams: List[Type.Param]): Unit = {
    argumentListTraverser.traverse(args = typeParams,
      argTraverser = typeParamArgTraverser,
      context = ArgumentListContext(options = ListTraversalOptions(maybeEnclosingDelimiter = Some(AngleBracket), onSameLine = true)))
  }
}
