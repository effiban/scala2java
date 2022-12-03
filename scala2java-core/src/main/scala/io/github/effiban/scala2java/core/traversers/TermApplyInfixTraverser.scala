package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InvocationArgListContext
import io.github.effiban.scala2java.core.transformers.TermApplyInfixToTermApplyTransformer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

private[traversers] class TermApplyInfixTraverserImpl(termTraverser: => TermTraverser,
                                                      termApplyTraverser: => TermApplyTraverser,
                                                      termNameTraverser: => TermNameTraverser,
                                                      invocationArgListTraverser: => InvocationArgListTraverser,
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
    termTraverser.traverse(termApplyInfix.lhs)
    write(" ")
    termNameTraverser.traverse(termApplyInfix.op)
    write(" ")
    //TODO handle type args
    termApplyInfix.args match {
      case Nil => throw new IllegalStateException("An Term.ApplyInfix must have at least one RHS arg")
      case arg :: Nil => termTraverser.traverse(arg)
      case args =>
        //TODO - fix (should transform to Term.Apply, cannot use infix notation in Java with multiple RHS args)
        val invocationArgListContext = InvocationArgListContext(onSameLine = true, argNameAsComment = true)
        invocationArgListTraverser.traverse(args, invocationArgListContext)
    }
  }
}
