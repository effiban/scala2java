package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.traversers.results.PkgTraversalResult
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

import scala.meta.{Import, Pkg}

trait PkgTraverser {
  def traverse(pkg: Pkg): PkgTraversalResult
}

private[traversers] class PkgTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                           pkgStatListTraverser: => PkgStatListTraverser,
                                           additionalImportersProvider: AdditionalImportersProvider) extends PkgTraverser {

  override def traverse(pkg: Pkg): PkgTraversalResult = {
    val traversedPkgRef = defaultTermRefTraverser.traverse(pkg.ref)
    val enrichedPkgStats = Import(additionalImportersProvider.provide()) +: pkg.stats
    val pkgStatListResult = pkgStatListTraverser.traverse(enrichedPkgStats)

    PkgTraversalResult(
      pkgRef = traversedPkgRef,
      statResults = pkgStatListResult.statResults,
      sealedHierarchies = pkgStatListResult.sealedHierarchies
    )
  }
}
