package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses

import scala.meta.Term

trait TermParamListTraverser {
  def traverse(termParams: List[Term.Param], onSameLine: Boolean = false): Unit
}

private[scala2java] class TermParamListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                     termParamTraverser: => TermParamTraverser) extends TermParamListTraverser {

  override def traverse(termParams: List[Term.Param], onSameLine: Boolean = false): Unit = {
    argumentListTraverser.traverse(args = termParams,
      argTraverser = termParamTraverser,
      onSameLine = onSameLine,
      maybeEnclosingDelimiter = Some(Parentheses))
  }
}

object TermParamListTraverser extends TermParamListTraverserImpl(ArgumentListTraverser, TermParamTraverser)
