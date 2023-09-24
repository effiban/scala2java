package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Import, Importer, Stat}

trait StatsByImportSplitter {
  def split(stats: List[Stat]): (List[Importer], List[Stat])
}

private[importmanipulation] class StatsByImportSplitterImpl(importFlattener: ImportFlattener) extends StatsByImportSplitter {

  def split(stats: List[Stat]): (List[Importer], List[Stat]) = {
    val imports = stats.collect { case `import`: Import => `import` }
    val importers = importFlattener.flatten(imports)
    val nonImports = stats.filterNot(stat => imports.exists(_.structure == stat.structure))
    (importers, nonImports)
  }
}

object StatsByImportSplitter extends StatsByImportSplitterImpl(ImportFlattener)