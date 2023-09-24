package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer, Term}

trait TermSelectImporterMatcher {
  def findMatch(termSelect: Term.Select, importer: Importer): Option[Importer]
}

object TermSelectImporterMatcher extends TermSelectImporterMatcher {

  override def findMatch(termSelect: Term.Select, importer: Importer): Option[Importer] = {
    if (qualMatchesRef(termSelect, importer)) findByMatchingImportee(termSelect, importer) else None
  }

  private def qualMatchesRef(termSelect: Term.Select, importer: Importer) = {
    // TODO support partial match (when full importer is a prefix of qual)
    termSelect.qual.structure == importer.ref.structure
  }

  private def findByMatchingImportee(termSelect: Term.Select, importer: Importer) = {
    // TODO use semantic information to match against wildcards
    importer.importees.collectFirst {
      case importee if matchesAnyImportee(termSelect, importer) => importer.copy(importees = List(importee))
    }
  }

  private def matchesAnyImportee(termSelect: Term.Select, importer: Importer) = {
    importer.importees.exists {
      case Importee.Name(name) if name.value == termSelect.name.value => true
      case Importee.Wildcard() => true
      case _ => false
    }
  }
}
