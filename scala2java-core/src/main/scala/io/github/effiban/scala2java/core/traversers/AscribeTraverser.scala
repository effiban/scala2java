package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Ascribe

trait AscribeTraverser extends ScalaTreeTraverser1[Ascribe]

private[traversers] class AscribeTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                               typeTraverser: => TypeTraverser) extends AscribeTraverser {

  // Explicitly specified type, e.g.: 2:Short
  // Java equivalent is casting. e.g. (short)2
  override def traverse(ascribe: Ascribe): Ascribe = {
    val traversedExpr = expressionTermTraverser.traverse(ascribe.expr)
    val traversedType = typeTraverser.traverse(ascribe.tpe)
    Ascribe(traversedExpr, traversedType)
  }
}
