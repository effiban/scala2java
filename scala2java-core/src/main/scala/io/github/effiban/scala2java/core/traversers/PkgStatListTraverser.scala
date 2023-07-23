package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver
import io.github.effiban.scala2java.core.traversers.results.{MultiStatTraversalResult, PopulatedStatTraversalResult}

import scala.meta.Stat

trait PkgStatListTraverser {
  def traverse(stats: List[Stat]): MultiStatTraversalResult
}

private[traversers] class PkgStatListTraverserImpl(pkgStatTraverser: => PkgStatTraverser,
                                                   sealedHierarchiesResolver: SealedHierarchiesResolver) extends PkgStatListTraverser {

  // TODO handle multiple definitions which are illegal in Java (such as 2 public classes in the same file)
  override def traverse(stats: List[Stat]): MultiStatTraversalResult = {
    val sealedHierarchies = sealedHierarchiesResolver.traverse(stats)
    val statResults = stats.map(stat => pkgStatTraverser.traverse(stat, sealedHierarchies))
      .collect { case result: PopulatedStatTraversalResult => result }
    MultiStatTraversalResult(statResults)
  }
}
