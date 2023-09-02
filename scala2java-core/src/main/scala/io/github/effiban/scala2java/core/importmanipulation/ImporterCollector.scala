package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Import, Importer, Stat}

trait ImporterCollector {
  def collectFlat(stats: List[Stat]): List[Importer]
}

private[importmanipulation] class ImporterCollectorImpl(importFlattener: ImportFlattener) extends ImporterCollector {

  def collectFlat(stats: List[Stat]): List[Importer] = {
    val imports = stats.collect { case `import`: Import => `import` }
    importFlattener.flatten(imports)
  }
}

object ImporterCollector extends ImporterCollectorImpl(ImportFlattener)