package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, StatRenderContext}
import io.github.effiban.scala2java.core.traversers.results.{PkgTraversalResult, StatTraversalResult}

trait StatRenderContextFactory {
  def apply(statTraversalResult: StatTraversalResult): StatRenderContext
}

private[contextfactories] class StatRenderContextFactoryImpl(pkgRenderContextFactory: => PkgRenderContextFactory)
  extends StatRenderContextFactory {

  override def apply(statTraversalResult: StatTraversalResult): StatRenderContext = statTraversalResult match {
    case pkgTraversalResult: PkgTraversalResult => pkgRenderContextFactory(pkgTraversalResult)
    case _ => EmptyStatRenderContext // TODO
  }
}
