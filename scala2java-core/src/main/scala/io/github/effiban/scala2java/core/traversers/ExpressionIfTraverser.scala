package io.github.effiban.scala2java.core.traversers

import scala.meta.Lit
import scala.meta.Term.If

trait ExpressionIfTraverser extends ScalaTreeTraverser1[If]

private[traversers] class ExpressionIfTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser) extends ExpressionIfTraverser {

  // An 'if' construct in the context of an evaluated expression, which must be able to return a value
  // and therefore the 'else' clause is mandatory for this case.
  override def traverse(`if`: If): If = {
    val traversedCond = expressionTermTraverser.traverse(`if`.cond)
    val traversedThenp = expressionTermTraverser.traverse(`if`.thenp)
    val traversedElsep = `if`.elsep match {
      case Lit.Unit() => throw new IllegalStateException("Trying to traverse an 'if' as an expression with no 'else' clause")
      case elsep => expressionTermTraverser.traverse(elsep)
    }
    If(
      cond = traversedCond,
      thenp = traversedThenp,
      elsep = traversedElsep
    )
  }
}
