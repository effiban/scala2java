package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer, Type}

trait TypeSelectImporterMatcher {
  def matches(typeSelect: Type.Select, importer: Importer): Boolean
}

object TypeSelectImporterMatcher extends TypeSelectImporterMatcher {

  // NOTE: assuming the importer has only one importee
  override def matches(typeSelect: Type.Select, importer: Importer): Boolean = {
    qualMatchesRef(typeSelect, importer) && nameMatchesImportee(typeSelect, importer)
  }

  private def qualMatchesRef(typeSelect: Type.Select, importer: Importer) = {
    typeSelect.qual.structure == importer.ref.structure
  }

  private def nameMatchesImportee(typeSelect: Type.Select, importer: Importer) = {
    importer.importees.head match {
      case Importee.Name(name) if name.value == typeSelect.name.value => true
      case Importee.Wildcard() => true
      case _ => false
    }
  }
}
