package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Import, Pkg}

trait PkgImportAdder {
  def addTo(pkg: Pkg): Pkg
}

private[importmanipulation] class PkgImportAdderImpl(statsByImportSplitter: StatsByImportSplitter,
                                                     importerDeduplicater: ImporterDeduplicater,
                                                     treeImporterGenerator: TreeImporterGenerator) extends PkgImportAdder {

  override def addTo(pkg: Pkg): Pkg = {
    val (initialImporters, nonImports) = statsByImportSplitter.split(pkg.stats)
    val additionalImporters = nonImports.flatMap(treeImporterGenerator.generate)
    val finalImporters = importerDeduplicater.dedup(initialImporters ++ additionalImporters)
    val finalImports = finalImporters.map(importer => Import(List(importer)))

    pkg.copy(stats = finalImports ++ nonImports)
  }
}

object PkgImportAdder extends PkgImportAdderImpl(
  StatsByImportSplitter,
  ImporterDeduplicater,
  TreeImporterGenerator
)
