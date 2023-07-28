package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, StatRenderContext}
import io.github.effiban.scala2java.core.traversers.results.{DeclTraversalResult, DefnTraversalResult, PkgTraversalResult, StatTraversalResult}

trait DefaultStatRenderContextFactory {
  def apply(statTraversalResult: StatTraversalResult, sealedHierarchies: SealedHierarchies = SealedHierarchies()): StatRenderContext
}

private[contextfactories] class DefaultStatRenderContextFactoryImpl(pkgRenderContextFactory: => PkgRenderContextFactory,
                                                                    declRenderContextFactory: => DeclRenderContextFactory,
                                                                    defnRenderContextFactory: => DefnRenderContextFactory)
  extends DefaultStatRenderContextFactory {

  override def apply(statTraversalResult: StatTraversalResult, sealedHierarchies: SealedHierarchies = SealedHierarchies()): StatRenderContext =
    statTraversalResult match {
      case pkgTraversalResult: PkgTraversalResult => pkgRenderContextFactory(pkgTraversalResult)
      case declTraversalResult: DeclTraversalResult => declRenderContextFactory(declTraversalResult)
      case defnTraversalResult: DefnTraversalResult => defnRenderContextFactory(defnTraversalResult, sealedHierarchies)
      case _ => EmptyStatRenderContext
    }
}
