package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Import, Pkg, Stat}

trait PkgImportRemover {
  def removeUnusedFrom(pkg: Pkg): Pkg
}

private[importmanipulation] class PkgImportRemoverImpl(importerCollector: ImporterCollector, treeImporterUsed: TreeImporterUsed)
  extends PkgImportRemover {

  override def removeUnusedFrom(pkg: Pkg): Pkg = {
    val nonImports = collectNonImports(pkg)

    val initialImporters = importerCollector.collectFlat(pkg.stats)
    val finalImports = initialImporters.filterNot(importer => treeImporterUsed(pkg, importer))
      .map(importer => Import(List(importer)))

    pkg.copy(stats = finalImports ++ nonImports)
  }

  private def collectNonImports(pkg: Pkg) = {
    pkg.stats.collect {
      case _: Import => None
      case stat: Stat => Some(stat)
    }.flatten
  }
}

object PkgImportRemover extends PkgImportRemoverImpl(ImporterCollector, TreeImporterUsed)
