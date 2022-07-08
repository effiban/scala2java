package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

private[scala2java] class TermApplyInfixTraverserImpl(termTraverser: => TermTraverser,
                                                      termNameTraverser: => TermNameTraverser,
                                                      termListTraverser: => TermListTraverser)
                                                     (implicit javaEmitter: JavaEmitter) extends TermApplyInfixTraverser {

  import javaEmitter._

  // Infix method invocation, e.g.: a + b
  override def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    //TODO - In Java will only work for operators,  need to check and handle differently for other methods
    termTraverser.traverse(termApplyInfix.lhs)
    emit(" ")
    termNameTraverser.traverse(termApplyInfix.op)
    emit(" ")
    //TODO handle type args
    //TODO - verify implementation for multiple RHS args
    termListTraverser.traverse(termApplyInfix.args, onSameLine = true)
  }
}

object TermApplyInfixTraverser extends TermApplyInfixTraverserImpl(TermTraverser, TermNameTraverser, TermListTraverser)