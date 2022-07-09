package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

private[traversers] class TermApplyInfixTraverserImpl(termTraverser: => TermTraverser,
                                                      termNameTraverser: => TermNameTraverser,
                                                      termListTraverser: => TermListTraverser)
                                                     (implicit javaWriter: JavaWriter) extends TermApplyInfixTraverser {

  import javaWriter._

  // Infix method invocation, e.g.: a + b
  override def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    //TODO - In Java will only work for operators,  need to check and handle differently for other methods
    termTraverser.traverse(termApplyInfix.lhs)
    write(" ")
    termNameTraverser.traverse(termApplyInfix.op)
    write(" ")
    //TODO handle type args
    //TODO - verify implementation for multiple RHS args
    termListTraverser.traverse(termApplyInfix.args, onSameLine = true)
  }
}
