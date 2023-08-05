package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

import scala.meta.{Import, Pkg}

trait PkgTraverser {
  def traverse(pkg: Pkg): Pkg
}

private[traversers] class PkgTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                           pkgStatListTraverser: => PkgStatListTraverser,
                                           additionalImportersProvider: AdditionalImportersProvider) extends PkgTraverser {

  override def traverse(pkg: Pkg): Pkg = {
    val traversedPkgRef = defaultTermRefTraverser.traverse(pkg.ref)
    val enrichedPkgStats = Import(additionalImportersProvider.provide()) +: pkg.stats
    val pkgStatListResult = pkgStatListTraverser.traverse(enrichedPkgStats)

    Pkg(
      ref = traversedPkgRef,
      stats = pkgStatListResult.statResults.map(_.tree)
    )
  }
}
