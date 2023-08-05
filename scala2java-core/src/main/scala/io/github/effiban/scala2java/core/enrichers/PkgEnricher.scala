package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver

import scala.meta.Pkg

trait PkgEnricher {
  def enrich(pkg: Pkg): EnrichedPkg
}

private[enrichers] class PkgEnricherImpl(pkgStatEnricher: => PkgStatEnricher,
                                         sealedHierarchiesResolver: SealedHierarchiesResolver) extends PkgEnricher {

  // TODO handle multiple definitions which are illegal in Java (such as 2 public classes in the same file)
  override def enrich(pkg: Pkg): EnrichedPkg = {
    val sealedHierarchies = sealedHierarchiesResolver.traverse(pkg.stats)
    val statResults = pkg.stats.map(stat => pkgStatEnricher.enrich(stat, sealedHierarchies))
    EnrichedPkg(pkg.ref, statResults, sealedHierarchies)
  }
}
