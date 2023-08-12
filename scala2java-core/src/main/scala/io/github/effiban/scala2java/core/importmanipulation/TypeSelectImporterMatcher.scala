package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer, Type}

trait TypeSelectImporterMatcher {
  def findMatch(typeSelect: Type.Select, importer: Importer): Option[Importer]
}

object TypeSelectImporterMatcher extends TypeSelectImporterMatcher {

  override def findMatch(typeSelect: Type.Select, importer: Importer): Option[Importer] = {
    if (qualMatchesRef(typeSelect, importer)) findByMatchingImportee(typeSelect, importer) else None
  }

  private def qualMatchesRef(typeSelect: Type.Select, importer: Importer) = {
    // TODO support partial match (when importer is a prefix)
    typeSelect.qual.structure == importer.ref.structure
  }

  private def findByMatchingImportee(typeSelect: Type.Select, importer: Importer) = {
    importer.importees.collectFirst {
      case importee if matchesAnyImportee(typeSelect, importer) => importer.copy(importees = List(importee))
    }
  }

  private def matchesAnyImportee(typeSelect: Type.Select, importer: Importer) = {
    importer.importees.exists {
      case Importee.Name(name) if name.value == typeSelect.name.value => true
      case Importee.Wildcard() => true
      case _ => false
    }
  }
}
