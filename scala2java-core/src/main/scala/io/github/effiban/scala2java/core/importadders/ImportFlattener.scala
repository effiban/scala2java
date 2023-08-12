package io.github.effiban.scala2java.core.importadders

import scala.meta.{Import, Importer}

trait ImportFlattener {
  def flatten(imports: List[Import]): List[Importer]
}

object ImportFlattener extends ImportFlattener {

  def flatten(imports: List[Import]): List[Importer] = {
    imports.flatMap(`import` => `import`.importers)
      .flatMap { importer => importer.importees.map(importee => importer.copy(importees = List(importee))) }
  }
}
