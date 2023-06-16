package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, StatContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions

import scala.meta.Term

@deprecated
trait DeprecatedTermParamListTraverser {
  def traverse(termParams: List[Term.Param],
               context: StatContext,
               onSameLine: Boolean = false): Unit
}

@deprecated
private[traversers] class DeprecatedTermParamListTraverserImpl(argumentListTraverser: => DeprecatedArgumentListTraverser,
                                                               termParamArgTraverserFactory: => DeprecatedTermParamArgTraverserFactory) extends DeprecatedTermParamListTraverser {

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
      argTraverser = termParamArgTraverserFactory(context),
      context = ArgumentListContext(options = options)
    )
  }
}
