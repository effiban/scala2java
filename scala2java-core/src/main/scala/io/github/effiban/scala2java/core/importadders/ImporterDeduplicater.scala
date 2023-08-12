package io.github.effiban.scala2java.core.importadders

import scala.meta.{Importee, Importer}

trait ImporterDeduplicater {
  def dedup(importers: List[Importer]): List[Importer]
}

object ImporterDeduplicater extends ImporterDeduplicater {
  def dedup(importers: List[Importer]): List[Importer] = {
    importers.distinctBy { importer =>
      importer.importees.head match {
        case importeeName: Importee.Name => importeeName.name.value
        case _ => importer.structure
      }
    }
  }
}
