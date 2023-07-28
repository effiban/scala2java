package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, StatRenderContext}
import io.github.effiban.scala2java.core.traversers.results.{DeclTraversalResult, PkgTraversalResult, StatTraversalResult}

trait DefaultStatRenderContextFactory {
  def apply(statTraversalResult: StatTraversalResult, sealedHierarchies: SealedHierarchies = SealedHierarchies()): StatRenderContext
}

private[contextfactories] class DefaultStatRenderContextFactoryImpl(pkgRenderContextFactory: => PkgRenderContextFactory,
                                                                    declRenderContextFactory: => DeclRenderContextFactory)
  extends DefaultStatRenderContextFactory {

  override def apply(statTraversalResult: StatTraversalResult, sealedHierarchies: SealedHierarchies = SealedHierarchies()): StatRenderContext =
    statTraversalResult match {
      case pkgTraversalResult: PkgTraversalResult => pkgRenderContextFactory(pkgTraversalResult)
      case declTraversalResult: DeclTraversalResult => declRenderContextFactory(declTraversalResult)
      case _ => EmptyStatRenderContext // TODO
    }
}
