package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, StatContext}

import scala.meta.Term

@deprecated
private[traversers] class DeprecatedTermParamArgumentTraverser(termParamTraverser: => DeprecatedTermParamTraverser,
                                                               statContext: StatContext) extends DeprecatedArgumentTraverser[Term.Param] {
  override def traverse(termParam: Term.Param, argContext: ArgumentContext): Unit = termParamTraverser.traverse(termParam, statContext)
}
