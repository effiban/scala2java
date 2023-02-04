package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, StatContext}

import scala.meta.Term

private[traversers] class TermParamArgumentTraverser(termParamTraverser: => TermParamTraverser,
                                                     statContext: StatContext) extends ArgumentTraverser[Term.Param] {
  override def traverse(termParam: Term.Param, argContext: ArgumentContext): Unit = termParamTraverser.traverse(termParam, statContext)
}
