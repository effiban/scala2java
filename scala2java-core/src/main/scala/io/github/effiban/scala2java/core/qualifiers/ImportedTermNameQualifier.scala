package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermNameImporterMatcher

import scala.meta.{Importer, Member, Term}

trait ImportedTermNameQualifier {

  def qualify(termName: Term.Name, importers: List[Importer] = Nil): Option[Term]
}

private[qualifiers] class ImportedTermNameQualifierImpl(termNameImporterMatcher: TermNameImporterMatcher)
  extends ImportedTermNameQualifier {

  override def qualify(termName: Term.Name, importers: List[Importer] = Nil): Option[Term] =
    importers.map(importer => termNameImporterMatcher.findMatch(termName, importer))
      .collectFirst { case Some(importer) => importer }
      .map(importer => Term.Select(importer.ref, termName))
}

object ImportedTermNameQualifier extends ImportedTermNameQualifierImpl(TermNameImporterMatcher)
