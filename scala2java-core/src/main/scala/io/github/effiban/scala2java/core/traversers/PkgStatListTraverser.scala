package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver
import io.github.effiban.scala2java.core.traversers.results.PopulatedStatTraversalResult

import scala.meta.Stat

trait PkgStatListTraverser {
  def traverse(stats: List[Stat]): List[Stat]
}

private[traversers] class PkgStatListTraverserImpl(pkgStatTraverser: => PkgStatTraverser,
                                                   sealedHierarchiesResolver: SealedHierarchiesResolver) extends PkgStatListTraverser {

  override def traverse(stats: List[Stat]): List[Stat] = {
    val sealedHierarchies = sealedHierarchiesResolver.resolve(stats)
    val statResults = stats.map(stat => pkgStatTraverser.traverse(stat, sealedHierarchies))
      .collect { case result: PopulatedStatTraversalResult => result }
    statResults.map(_.tree)
  }
}
