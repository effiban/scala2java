package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Type

@deprecated
trait DeprecatedTypeParamListTraverser {
  def traverse(typeParams: List[Type.Param]): Unit
}

@deprecated
private[traversers] class DeprecatedTypeParamListTraverserImpl(argumentListTraverser: => DeprecatedArgumentListTraverser,
                                                               typeParamArgTraverser: => DeprecatedArgumentTraverser[Type.Param])
  extends DeprecatedTypeParamListTraverser {

  override def traverse(typeParams: List[Type.Param]): Unit = {
    argumentListTraverser.traverse(args = typeParams,
      argTraverser = typeParamArgTraverser,
      context = ArgumentListContext(options = ListTraversalOptions(maybeEnclosingDelimiter = Some(AngleBracket), onSameLine = true)))
  }
}
