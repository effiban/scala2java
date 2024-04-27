package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.entities.TermSelects.JavaLang

import scala.annotation.unused
import scala.meta.{Import, Importer, Pkg, Stat}

trait PkgImportRemover {
  def removeUnusedFrom(pkg: Pkg): Pkg

  def removeJavaLangFrom(pkg: Pkg): Pkg
}

private[importmanipulation] class PkgImportRemoverImpl(statsByImportSplitter: StatsByImportSplitter, treeImporterUsed: TreeImporterUsed)
  extends PkgImportRemover {

  override def removeUnusedFrom(pkg: Pkg): Pkg = removeFrom(pkg, isImporterUsed)

  override def removeJavaLangFrom(pkg: Pkg): Pkg = removeFrom(pkg, isNotJavaLangImporter)

  private def removeFrom(pkg: Pkg, isImporterRequired: (Importer, List[Stat]) => Boolean) = {
    val (initialImporters, nonImports) = statsByImportSplitter.split(pkg.stats)
    val finalImports = initialImporters.filter(importer => isImporterRequired(importer, nonImports))
      .map(importer => Import(List(importer)))

    pkg.copy(stats = finalImports ++ nonImports)
  }

  private def isImporterUsed(importer: Importer, nonImports: List[Stat]): Boolean = {
    nonImports.exists(nonImport => treeImporterUsed(nonImport, importer))
  }

  private def isNotJavaLangImporter(importer: Importer, @unused ignored: List[Stat]): Boolean = {
    importer.ref.structure != JavaLang.structure
  }
}

object PkgImportRemover extends PkgImportRemoverImpl(StatsByImportSplitter, TreeImporterUsed)
