package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

import scala.meta.{Import, Importer, Pkg, Stat}

trait PkgUnqualifier {
  def unqualify(pkg: Pkg): Pkg
}

private[unqualifiers] class PkgUnqualifierImpl(statsByImportSplitter: StatsByImportSplitter,
                                               treeUnqualifier: => TreeUnqualifier) extends PkgUnqualifier {

  override def unqualify(pkg: Pkg): Pkg = {
    val (importers, nonImports) = statsByImportSplitter.split(pkg.stats)
    val imports = importers.map(importer => Import(List(importer)))
    val unqualifiedStats = nonImports.map(stat => unqualify(stat, importers))

    pkg.copy(stats = imports ++ unqualifiedStats)
  }

  private def unqualify(stat: Stat, importers: List[Importer]): Stat = {
    treeUnqualifier.unqualify(stat, importers).asInstanceOf[Stat]
  }
}
