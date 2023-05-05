package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeWildcardTraverser extends ScalaTreeTraverser1[Type.Wildcard]

private[traversers] class TypeWildcardTraverserImpl(typeBoundsTraverser: => TypeBoundsTraverser) extends TypeWildcardTraverser {

  // Underscore in type param, e.g. T[_] with possible bounds e.g. T[_ <: A]
  override def traverse(wildcardType: Type.Wildcard): Type.Wildcard = {
    wildcardType.copy(bounds = typeBoundsTraverser.traverse(wildcardType.bounds))
  }
}
