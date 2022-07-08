package effiban.scala2java.traversers

import effiban.scala2java.{JavaEmitter, Parentheses}

import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser[Term.Apply]

private[scala2java] class TermApplyTraverserImpl(termTraverser: => TermTraverser,
                                                 termListTraverser: => TermListTraverser)
                                                (implicit javaEmitter: JavaEmitter) extends TermApplyTraverser {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    termTraverser.traverse(termApply.fun)
    termListTraverser.traverse(termApply.args, maybeDelimiterType = Some(Parentheses))
  }
}

object TermApplyTraverser extends TermApplyTraverserImpl(TermTraverser, TermListTraverser)