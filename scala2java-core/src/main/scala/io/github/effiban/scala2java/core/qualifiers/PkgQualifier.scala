package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

import scala.meta.{Import, Importer, Pkg, Stat}

trait PkgQualifier {
  def qualify(pkg: Pkg): Pkg
}

private[qualifiers] class PkgQualifierImpl(statsByImportSplitter: StatsByImportSplitter,
                                           treeQualifier: => TreeQualifier) extends PkgQualifier {
  override def qualify(pkg: Pkg): Pkg = {
    val (importers, nonImports) = statsByImportSplitter.split(pkg.stats)
    val imports = importers.map(importer => Import(List(importer)))
    val qualifiedStats = nonImports.map(stat => qualify(stat, importers))

    pkg.copy(stats = imports ++ qualifiedStats)
  }

  private def qualify(stat: Stat, importers: List[Importer]): Stat = {
    treeQualifier.qualify(stat, QualificationContext(importers)).asInstanceOf[Stat]
  }
}
