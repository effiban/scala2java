package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.EnrichedPkg
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDecl, EnrichedDefn, EnrichedStat}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, StatRenderContext}

trait DefaultStatRenderContextFactory {

  def apply(enrichedStat: EnrichedStat, sealedHierarchies: SealedHierarchies = SealedHierarchies()): StatRenderContext
}

private[contextfactories] class DefaultStatRenderContextFactoryImpl(pkgRenderContextFactory: => PkgRenderContextFactory,
                                                                    declRenderContextFactory: => DeclRenderContextFactory,
                                                                    defnRenderContextFactory: => DefnRenderContextFactory)
  extends DefaultStatRenderContextFactory {

  override def apply(enrichedStat: EnrichedStat, sealedHierarchies: SealedHierarchies = SealedHierarchies()): StatRenderContext =
    enrichedStat match {
      case enrichedPkg: EnrichedPkg => pkgRenderContextFactory(enrichedPkg)
      case enrichedDecl: EnrichedDecl => declRenderContextFactory(enrichedDecl)
      case enrichedDefn: EnrichedDefn => defnRenderContextFactory(enrichedDefn, sealedHierarchies)
      case _ => EmptyStatRenderContext
    }
}
