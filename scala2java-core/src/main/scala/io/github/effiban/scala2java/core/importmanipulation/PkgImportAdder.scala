package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Import, Pkg, Stat}

trait PkgImportAdder {
  def addTo(pkg: Pkg): Pkg
}

private[importmanipulation] class PkgImportAdderImpl(importFlattener: ImportFlattener,
                                                     importerDeduplicater: ImporterDeduplicater,
                                                     treeImporterGenerator: TreeImporterGenerator) extends PkgImportAdder {

  override def addTo(pkg: Pkg): Pkg = {
    val initialImports = collectImports(pkg)
    val nonImports = collectNonImports(pkg)

    val initialImporters = importFlattener.flatten(initialImports)
    val additionalImporters = treeImporterGenerator.generate(pkg)

    val finalImporters = importerDeduplicater.dedup(initialImporters ++ additionalImporters)
    val finalImports = finalImporters.map(importer => Import(List(importer)))

    pkg.copy(stats = finalImports ++ nonImports)
  }

  private def collectImports(pkg: Pkg) = {
    pkg.stats.collect { case `import`: Import => `import` }
  }

  private def collectNonImports(pkg: Pkg) = {
    pkg.stats.collect {
      case _: Import => None
      case stat: Stat => Some(stat)
    }.flatten
  }
}

object PkgImportAdder extends PkgImportAdderImpl(
  ImportFlattener,
  ImporterDeduplicater,
  TreeImporterGenerator
)
