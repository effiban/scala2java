package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer, Term}

trait TermNameImporterMatcher {
  def findMatch(termName: Term.Name, importer: Importer): Option[Importer]
}

object TermNameImporterMatcher extends TermNameImporterMatcher {

  override def findMatch(termName: Term.Name, importer: Importer): Option[Importer] = {
    // TODO use semantic information to match against wildcards
    importer.importees.collectFirst {
      case importee@Importee.Name(name) if name.value == termName.value => importer.copy(importees = List(importee))
    }
  }
}
