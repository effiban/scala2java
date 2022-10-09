package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.entities.ListTraversalOptions

import scala.meta.Type

trait TypeParamListTraverser {
  def traverse(typeParams: List[Type.Param]): Unit
}

private[traversers] class TypeParamListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                     typeParamTraverser: => TypeParamTraverser) extends TypeParamListTraverser {

  override def traverse(typeParams: List[Type.Param]): Unit = {
    argumentListTraverser.traverse(args = typeParams,
      argTraverser = typeParamTraverser,
      ListTraversalOptions(maybeEnclosingDelimiter = Some(AngleBracket), onSameLine = true))
  }
}
