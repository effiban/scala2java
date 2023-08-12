package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer}

trait ImporterDeduplicater {
  def dedup(importers: List[Importer]): List[Importer]
}

object ImporterDeduplicater extends ImporterDeduplicater {

  // NOTE: Assuming this is called only after the importers have been flattened to one importee each
  def dedup(importers: List[Importer]): List[Importer] = {
    Function.chain(List(exactDeduper, wildcardWithNamesDeduper, nameDeduper))(importers)
  }

  private def exactDeduper: List[Importer] => List[Importer] = _.distinctBy(_.structure)

  // If both a wildcard and individual imports exist for same prefix, retain the wildcard only
  private def wildcardWithNamesDeduper: List[Importer] => List[Importer] = importers =>
    importers.groupBy(_.ref.structure).view
      .mapValues(curImporters => {
        curImporters.collectFirst {
          case wildCardImporter@Importer(_, List(Importee.Wildcard())) => wildCardImporter
        } match {
          case Some(wildCardImporter) => List(wildCardImporter)
          case None => curImporters
        }
      })
      .values.flatten.toList
      // Restoring the same order as the input, after the intermediate conversion to a Map.
      // The final sorting of imports in the Java file ("organize imports") may be implemented later on,
      // but in any case this method should not affect the input order
      .sortBy(importer => importers.indexWhere(_.structure == importer.structure))

  private def nameDeduper: List[Importer] => List[Importer] = importers =>
    importers.distinctBy { importer =>
      importer.importees.head match {
        case importeeName: Importee.Name => importeeName.name.value
        case importer => importer
      }
    }
}
