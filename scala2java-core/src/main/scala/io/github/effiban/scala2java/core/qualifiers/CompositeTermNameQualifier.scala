package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermNameImporterMatcher

import scala.meta.{Importer, Term}

trait CompositeTermNameQualifier {

  def qualify(termName: Term.Name, importers: List[Importer] = Nil): Term
}

private[qualifiers] class CompositeTermNameQualifierImpl(termNameImporterMatcher: TermNameImporterMatcher,
                                                         coreTermNameQualifier: CoreTermNameQualifier)
  extends CompositeTermNameQualifier {

  override def qualify(termName: Term.Name, importers: List[Importer] = Nil): Term = {
    importers.map(importer => termNameImporterMatcher.findMatch(termName, importer))
      .collectFirst { case Some(importer) => importer }
      .map(importer => Term.Select(importer.ref, termName))
      .orElse(coreTermNameQualifier.qualify(termName))
      .getOrElse(termName)
  }
}

object CompositeTermNameQualifier extends CompositeTermNameQualifierImpl(TermNameImporterMatcher, CoreTermNameQualifier)
