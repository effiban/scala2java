package effiban.scala2java.traversers

import effiban.scala2java.classifiers.TermApplyInfixClassifier
import effiban.scala2java.contexts.InvocationArgListContext
import effiban.scala2java.transformers.{TermApplyInfixToMapEntryTransformer, TermApplyInfixToRangeTransformer}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

private[traversers] class TermApplyInfixTraverserImpl(termTraverser: => TermTraverser,
                                                      termApplyTraverser: => TermApplyTraverser,
                                                      termNameTraverser: => TermNameTraverser,
                                                      invocationArgListTraverser: => InvocationArgListTraverser,
                                                      termApplyInfixClassifier: TermApplyInfixClassifier,
                                                      termApplyInfixToRangeTransformer: TermApplyInfixToRangeTransformer,
                                                      termApplyInfixToMapEntryTransformer: TermApplyInfixToMapEntryTransformer)
                                                     (implicit javaWriter: JavaWriter) extends TermApplyInfixTraverser {

  import javaWriter._

  // Infix method invocation, e.g.: a + b, 0 until 5, a -> 1
  override def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    termApplyInfix match {
      case theTermApplyInfix if termApplyInfixClassifier.isRange(theTermApplyInfix) => traverseRange(theTermApplyInfix)
      case theTermApplyInfix if termApplyInfixClassifier.isAssociation(theTermApplyInfix) => traverseAssociation(theTermApplyInfix)
      // TODO handle additional non-operator methods which should be transformed into regular (prefix) order in Java
      case _ => traverseSimple(termApplyInfix)
    }
  }

  private def traverseRange(termApplyInfix: Term.ApplyInfix): Unit = {
    termApplyTraverser.traverse(termApplyInfixToRangeTransformer.transform(termApplyInfix))
  }

  private def traverseAssociation(termApplyInfix: Term.ApplyInfix): Unit = {
    termApplyTraverser.traverse(termApplyInfixToMapEntryTransformer.transform(termApplyInfix))
  }

  private def traverseSimple(termApplyInfix: Term.ApplyInfix): Unit = {
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
