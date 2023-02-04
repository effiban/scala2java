package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext

import scala.meta.Term

trait TermParamArgTraverserFactory {

  def apply(statContext: StatContext): ArgumentTraverser[Term.Param]
}

class TermParamArgTraverserFactoryImpl(termParamTraverser: => TermParamTraverser) extends TermParamArgTraverserFactory {

  override def apply(statContext: StatContext): ArgumentTraverser[Term.Param] = new TermParamArgumentTraverser(termParamTraverser, statContext)
}
