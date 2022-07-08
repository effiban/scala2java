package effiban.scala2java

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
      maybeDelimiterType = Some(Parentheses))
  }
}

object TermParamListTraverser extends TermParamListTraverserImpl(ArgumentListTraverser, TermParamTraverser)
