package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermNameImporterMatcher

import scala.meta.{Importer, Member, Term}

trait CompositeTermNameQualifier {

  def qualify(termName: Term.Name, context: QualificationContext = QualificationContext()): Term
}

private[qualifiers] class CompositeTermNameQualifierImpl(termNameImporterMatcher: TermNameImporterMatcher,
                                                         coreTermNameQualifier: CoreTermNameQualifier)
  extends CompositeTermNameQualifier {

  override def qualify(termName: Term.Name, context: QualificationContext = QualificationContext()): Term = termName.parent match {
    case Some(_: Member.Term | _: Term.Param) => termName
    case _ => qualifyInner(termName, context)
  }

  private def qualifyInner(termName: Term.Name, context: QualificationContext): Term = {
    context.importers.map(importer => termNameImporterMatcher.findMatch(termName, importer))
      .collectFirst { case Some(importer) => importer }
      .map(importer => Term.Select(importer.ref, termName))
      .orElse(coreTermNameQualifier.qualify(termName))
      .getOrElse(termName)
  }
}

object CompositeTermNameQualifier extends CompositeTermNameQualifierImpl(TermNameImporterMatcher, CoreTermNameQualifier)
