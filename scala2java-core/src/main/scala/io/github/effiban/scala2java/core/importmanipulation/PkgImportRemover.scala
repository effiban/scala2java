package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Import, Importer, Pkg, Stat}

trait PkgImportRemover {
  def removeUnusedFrom(pkg: Pkg): Pkg
}

private[importmanipulation] class PkgImportRemoverImpl(statsByImportSplitter: StatsByImportSplitter, treeImporterUsed: TreeImporterUsed)
  extends PkgImportRemover {

  override def removeUnusedFrom(pkg: Pkg): Pkg = {
    val (initialImporters, nonImports) = statsByImportSplitter.split(pkg.stats)
    val finalImports = initialImporters.filter(importer => importerUsed(importer, nonImports))
      .map(importer => Import(List(importer)))

    pkg.copy(stats = finalImports ++ nonImports)
  }

  def importerUsed(importer: Importer, nonImports: List[Stat]): Boolean = {
    nonImports.exists(nonImport => treeImporterUsed(nonImport, importer))
  }
}

object PkgImportRemover extends PkgImportRemoverImpl(StatsByImportSplitter, TreeImporterUsed)
