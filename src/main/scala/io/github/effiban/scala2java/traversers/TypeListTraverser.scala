package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.entities.EnclosingDelimiter._
import io.github.effiban.scala2java.entities.ListTraversalOptions

import scala.meta.Type

trait TypeListTraverser {
  def traverse(types: List[Type]): Unit
}

private[traversers] class TypeListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                typeTraverser: => TypeTraverser) extends TypeListTraverser {

  override def traverse(types: List[Type]): Unit = {
    argumentListTraverser.traverse(args = types,
      // TODO - call the traverser with an argument indicating that Java primitives should be boxed
      argTraverser = typeTraverser,
      ListTraversalOptions(maybeEnclosingDelimiter = Some(AngleBracket), onSameLine = true)
    )
  }
}
