package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeWithTraverser extends ScalaTreeTraverser1[Type.With]

private[traversers] class TypeWithTraverserImpl(typeTraverser: => TypeTraverser) extends TypeWithTraverser {

  // type with parent, e.g. 'A with B' in: type X = A with B
  // approximated by Java "extends" but might not compile
  override def traverse(typeWith: Type.With): Type.With = {
    val traversedLhs = typeTraverser.traverse(typeWith.lhs)
    val traversedRhs = typeTraverser.traverse(typeWith.rhs)
    Type.With(traversedLhs, traversedRhs)
  }
}
