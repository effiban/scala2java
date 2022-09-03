package effiban.scala2java.traversers

import effiban.scala2java.classifiers.TermApplyInfixClassifier
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.transformers.TermApplyInfixToRangeTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

private[traversers] class TermApplyInfixTraverserImpl(termTraverser: => TermTraverser,
                                                      termApplyTraverser: => TermApplyTraverser,
                                                      termNameTraverser: => TermNameTraverser,
                                                      termListTraverser: => TermListTraverser,
                                                      termApplyInfixClassifier: TermApplyInfixClassifier,
                                                      termApplyInfixToRangeTransformer: TermApplyInfixToRangeTransformer)
                                                     (implicit javaWriter: JavaWriter) extends TermApplyInfixTraverser {

  import javaWriter._

  // Infix method invocation, e.g.: a + b, 0 until 5
  override def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    termApplyInfix match {
      case theTermApplyInfix if termApplyInfixClassifier.isRange(theTermApplyInfix) => traverseRange(theTermApplyInfix)
      // TODO handle additional non-operator methods which should be transformed into regular (prefix) order in Java
      case _ => traverseSimple(termApplyInfix)
    }
  }

  private def traverseRange(termApplyInfix: Term.ApplyInfix): Unit = {
    termApplyTraverser.traverse(termApplyInfixToRangeTransformer.transform(termApplyInfix))
  }

  private def traverseSimple(termApplyInfix: Term.ApplyInfix): Unit = {
    termTraverser.traverse(termApplyInfix.lhs)
    write(" ")
    termNameTraverser.traverse(termApplyInfix.op)
    write(" ")
    //TODO handle type args
    //TODO - verify implementation for multiple RHS args
    termListTraverser.traverse(termApplyInfix.args, ListTraversalOptions(onSameLine = true))
  }
}
