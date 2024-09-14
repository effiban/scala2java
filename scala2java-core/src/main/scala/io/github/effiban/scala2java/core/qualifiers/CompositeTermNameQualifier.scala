package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Member, Term}

trait CompositeTermNameQualifier {

  def qualify(termName: Term.Name, context: QualificationContext = QualificationContext()): Term
}

private[qualifiers] class CompositeTermNameQualifierImpl(inheritedTermNameQualifier: InheritedTermNameQualifier,
                                                         importedTermNameQualifier: ImportedTermNameQualifier,
                                                         coreTermNameQualifier: CoreTermNameQualifier)
  extends CompositeTermNameQualifier {

  override def qualify(termName: Term.Name, context: QualificationContext = QualificationContext()): Term = termName.parent match {
    case Some(_: Member.Term | _: Term.Param) => termName
    case _ => qualifyInner(termName, context)
  }

  private def qualifyInner(termName: Term.Name, context: QualificationContext): Term = {
    LazyList(
      inheritedTermNameQualifier.qualify _,
      aTermName => importedTermNameQualifier.qualify(aTermName, context.importers),
      coreTermNameQualifier.qualify _
    ).map(_.apply(termName))
      .collectFirst { case Some(term) => term }
      .getOrElse(termName)
  }
}

object CompositeTermNameQualifier extends CompositeTermNameQualifierImpl(
  InheritedTermNameQualifier,
  ImportedTermNameQualifier,
  CoreTermNameQualifier)
