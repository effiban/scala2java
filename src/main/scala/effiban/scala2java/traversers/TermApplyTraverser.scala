package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser[Term.Apply]

private[scala2java] class TermApplyTraverserImpl(termTraverser: => TermTraverser,
                                                 termListTraverser: => TermListTraverser)
                                                (implicit javaWriter: JavaWriter) extends TermApplyTraverser {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    termTraverser.traverse(termApply.fun)
    termListTraverser.traverse(termApply.args, maybeEnclosingDelimiter = Some(Parentheses))
  }
}

object TermApplyTraverser extends TermApplyTraverserImpl(TermTraverser, TermListTraverser)