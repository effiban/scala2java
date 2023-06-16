package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext

import scala.meta.Term

@deprecated
trait DeprecatedTermParamArgTraverserFactory {

  def apply(statContext: StatContext): DeprecatedArgumentTraverser[Term.Param]
}

@deprecated
class DeprecatedTermParamArgTraverserFactoryImpl(termParamTraverser: => DeprecatedTermParamTraverser) extends DeprecatedTermParamArgTraverserFactory {

  override def apply(statContext: StatContext): DeprecatedArgumentTraverser[Term.Param] = new DeprecatedTermParamArgumentTraverser(termParamTraverser, statContext)
}
