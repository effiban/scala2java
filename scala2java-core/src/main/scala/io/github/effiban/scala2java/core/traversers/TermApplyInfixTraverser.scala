package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser1[Term.ApplyInfix]

private[traversers] class TermApplyInfixTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser)
  extends TermApplyInfixTraverser {

  override def traverse(termApplyInfix: Term.ApplyInfix): Term.ApplyInfix = {
    //TODO handle type args
    val traversedLhs = expressionTermTraverser.traverse(termApplyInfix.lhs)
    val traversedRhs = termApplyInfix.args.map(expressionTermTraverser.traverse)
    termApplyInfix.copy(lhs = traversedLhs, args = traversedRhs)
  }
}
