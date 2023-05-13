package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

private[traversers] class TermApplyInfixTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                      termApplyTraverser: => TermApplyTraverser,
                                                      termNameRenderer: TermNameRenderer,
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
    //TODO handle type args
    termApplyInfix.args match {
      case Nil => throw new IllegalStateException("An Term.ApplyInfix must have at least one RHS arg")
      case arg :: Nil =>
        expressionTermTraverser.traverse(termApplyInfix.lhs)
        write(" ")
        termNameRenderer.render(termApplyInfix.op)
        write(" ")
        expressionTermTraverser.traverse(arg)
      case _ =>
        // If the infix was classified above as a Java-style infix, but has multiple RHS args - there is no Java equivalent
        write(s"UNSUPPORTED: $termApplyInfix")
    }
  }
}
