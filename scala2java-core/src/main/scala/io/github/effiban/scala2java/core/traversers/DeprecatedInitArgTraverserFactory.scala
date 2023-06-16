package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InitContext

import scala.meta.Init

@deprecated
trait DeprecatedInitArgTraverserFactory {

  def apply(initContext: InitContext): DeprecatedArgumentTraverser[Init]
}

@deprecated
class DeprecatedInitArgTraverserFactoryImpl(initTraverser: => DeprecatedInitTraverser) extends DeprecatedInitArgTraverserFactory {

  override def apply(initContext: InitContext): DeprecatedArgumentTraverser[Init] = new DeprecatedInitArgumentTraverser(initTraverser, initContext)
}
