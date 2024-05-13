package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait ExpressionTermSelectTraverser extends ScalaTreeTraverser2[Term.Select, Term]

private[traversers] class ExpressionTermSelectTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser)
  extends ExpressionTermSelectTraverser {

  // qualified name in the context of an evaluated expression, that might need to be transformed into a Java equivalent
  override def traverse(select: Term.Select): Term = {
    val traversedQual = expressionTermTraverser.traverse(select.qual)
    select.copy(qual = traversedQual)
  }
}
