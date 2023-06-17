package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser2[Term.ApplyInfix, Term]

private[traversers] class TermApplyInfixTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                      termApplyTraverser: => TermApplyTraverser,
                                                      termApplyInfixToTermApplyTransformer: TermApplyInfixToTermApplyTransformer)
  extends TermApplyInfixTraverser {

  // Infix method invocation, e.g.: a + b, 0 until 5, a -> 1
  // Except for Java-style operators, all other invocations must be transformed to a regular method invocation in Java
  override def traverse(termApplyInfix: Term.ApplyInfix): Term = {
    termApplyInfixToTermApplyTransformer.transform(termApplyInfix) match {
      case Some(termApply) => termApplyTraverser.traverse(termApply)
      case _ => traverseAsInfix(termApplyInfix)
    }
  }

  private def traverseAsInfix(termApplyInfix: Term.ApplyInfix): Term.ApplyInfix = {
    //TODO handle type args
    val traversedLhs = expressionTermTraverser.traverse(termApplyInfix.lhs)
    val traversedRhs = termApplyInfix.args match {
      case Nil => throw new IllegalStateException("An Term.ApplyInfix must have at least one RHS arg")
      // Ignoring any args beyond the first RHS arg - they are unsupported in Java
      case arg :: _ => expressionTermTraverser.traverse(arg)
    }
    termApplyInfix.copy(lhs = traversedLhs, args = List(traversedRhs))
  }
}
