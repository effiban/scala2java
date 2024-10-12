package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermSelectImporterMatcher
import io.github.effiban.scala2java.core.qualifiers.QualificationContext

import scala.meta.Term

trait TermSelectUnqualifier {

  def unqualify(termSelect: Term.Select, context: QualificationContext = QualificationContext()): Term.Ref
}

private[unqualifiers] class TermSelectUnqualifierImpl(termSelectImporterMatcher: TermSelectImporterMatcher,
                                                      superSelectUnqualifier: SuperSelectUnqualifier) extends TermSelectUnqualifier {

  override def unqualify(termSelect: Term.Select, context: QualificationContext = QualificationContext()): Term.Ref = {
    termSelect match {
      case Term.Select(termSuper: Term.Super, termName: Term.Name) =>
        superSelectUnqualifier.unqualify(termSuper, termName, termSelect.parent)
      case aTermSelect =>
        context.importers.map(importer => termSelectImporterMatcher.findMatch(termSelect, importer))
          .collectFirst {
            case Some(_) => aTermSelect.name
          }.getOrElse(aTermSelect)
    }
  }
}

object TermSelectUnqualifier extends TermSelectUnqualifierImpl(
  TermSelectImporterMatcher,
  SuperSelectUnqualifier
)
