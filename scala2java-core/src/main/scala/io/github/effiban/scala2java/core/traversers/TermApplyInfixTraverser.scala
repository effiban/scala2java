package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

private[traversers] class TermApplyInfixTraverserImpl(expressionTermTraverser: => TermTraverser,
                                                      termApplyTraverser: => TermApplyTraverser,
                                                      defaultTermNameTraverser: => TermNameTraverser,
                                                      argumentListTraverser: => ArgumentListTraverser,
                                                      invocationArgTraverser: => ArgumentTraverser[Term],
                                                      termApplyInfixToTermApplyTransformer: TermApplyInfixToTermApplyTransformer)
                                                     (implicit javaWriter: JavaWriter) extends TermApplyInfixTraverser {

  import javaWriter._

  // Infix method invocation, e.g.: a + b, 0 until 5, a -> 1
  // Except for Java-style operators, all other invocations must be transformed to a regular method invocation in Java
  override def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    termApplyInfixToTermApplyTransformer.transform(termApplyInfix) match {
      case Some(termApply) => termApplyTraverser.traverse(termApply)
      case _ => traverseAsInfix(termApplyInfix)
    }
  }

  private def traverseAsInfix(termApplyInfix: Term.ApplyInfix): Unit = {
    expressionTermTraverser.traverse(termApplyInfix.lhs)
    write(" ")
    defaultTermNameTraverser.traverse(termApplyInfix.op)
    write(" ")
    //TODO handle type args
    termApplyInfix.args match {
      case Nil => throw new IllegalStateException("An Term.ApplyInfix must have at least one RHS arg")
      case arg :: Nil => expressionTermTraverser.traverse(arg)
      case args =>
        //TODO - fix (should transform to Term.Apply, cannot use infix notation in Java with multiple RHS args)
        val options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))
        val argListContext = ArgumentListContext(maybeParent = Some(termApplyInfix), options = options, argNameAsComment = true)
        argumentListTraverser.traverse(
          args = args,
          argTraverser = invocationArgTraverser,
          context = argListContext
        )
    }
  }
}
