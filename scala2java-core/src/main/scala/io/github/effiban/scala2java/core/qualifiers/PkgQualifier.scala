package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter

import scala.meta.{Import, Importer, Pkg, Stat}

trait PkgQualifier {
  def qualify(pkg: Pkg): Pkg
}

private[qualifiers] class PkgQualifierImpl(statsByImportSplitter: StatsByImportSplitter,
                                           statQualifier: StatQualifier) extends PkgQualifier {
  override def qualify(pkg: Pkg): Pkg = {
    val (importers, nonImports) = statsByImportSplitter.split(pkg.stats)
    val imports = importers.map(importer => Import(List(importer)))
    val qualifiedStats = nonImports.map(stat => qualify(stat, importers))

    pkg.copy(stats = imports ++ qualifiedStats)
  }

  private def qualify(stat: Stat, importers: List[Importer]): Stat = {
    statQualifier.qualify(stat, QualificationContext(importers))
  }
}

object PkgQualifier extends PkgQualifierImpl(
  StatsByImportSplitter,
  StatQualifier
)