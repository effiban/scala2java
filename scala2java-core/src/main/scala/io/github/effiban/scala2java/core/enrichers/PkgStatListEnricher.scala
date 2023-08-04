package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver

import scala.meta.Stat

trait PkgStatListEnricher {
  def enrich(stats: List[Stat]): EnrichedPkgStatList
}

private[enrichers] class PkgStatListEnricherImpl(pkgStatEnricher: => PkgStatEnricher,
                                                 sealedHierarchiesResolver: SealedHierarchiesResolver) extends PkgStatListEnricher {

  // TODO handle multiple definitions which are illegal in Java (such as 2 public classes in the same file)
  override def enrich(stats: List[Stat]): EnrichedPkgStatList = {
    val sealedHierarchies = sealedHierarchiesResolver.traverse(stats)
    val statResults = stats.map(stat => pkgStatEnricher.enrich(stat, sealedHierarchies))
    EnrichedPkgStatList(statResults, sealedHierarchies)
  }
}
