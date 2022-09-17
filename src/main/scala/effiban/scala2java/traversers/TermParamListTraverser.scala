package effiban.scala2java.traversers

import effiban.scala2java.contexts.StatContext
import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions

import scala.meta.Term

trait TermParamListTraverser {
  def traverse(termParams: List[Term.Param],
               context: StatContext,
               onSameLine: Boolean = false): Unit
}

private[traversers] class TermParamListTraverserImpl(argumentListTraverser: => ArgumentListTraverser,
                                                     termParamTraverser: => TermParamTraverser) extends TermParamListTraverser {

  override def traverse(termParams: List[Term.Param],
                        context: StatContext,
                        onSameLine: Boolean = false): Unit = {
    val options = ListTraversalOptions(
      onSameLine = onSameLine,
      maybeEnclosingDelimiter = Some(Parentheses),
      traverseEmpty = true
    )

    argumentListTraverser.traverse(
      args = termParams,
      argTraverser = (termParam: Term.Param) => termParamTraverser.traverse(termParam, context),
      options = options
    )
  }
}
