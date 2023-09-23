package io.github.effiban.scala2java.core.traversers

import scala.meta.Pkg

trait PkgTraverser {
  def traverse(pkg: Pkg): Pkg
}

private[traversers] class PkgTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser,
                                           pkgStatListTraverser: => PkgStatListTraverser) extends PkgTraverser {

  override def traverse(pkg: Pkg): Pkg = {
    val traversedPkgRef = defaultTermRefTraverser.traverse(pkg.ref)
    val traversedPkgStats = pkgStatListTraverser.traverse(pkg.stats)

    Pkg(ref = traversedPkgRef, stats = traversedPkgStats)
  }
}
