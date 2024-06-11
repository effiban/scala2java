package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermSelectImporterMatcher
import io.github.effiban.scala2java.core.qualifiers.QualificationContext

import scala.meta.Term

trait TermSelectUnqualifier {

  def unqualify(termSelect: Term.Select, context: QualificationContext = QualificationContext()): Term.Ref
}

private[unqualifiers] class TermSelectUnqualifierImpl(termSelectImporterMatcher: TermSelectImporterMatcher) extends TermSelectUnqualifier {

  override def unqualify(termSelect: Term.Select, context: QualificationContext = QualificationContext()): Term.Ref = {
    context.importers.map(importer => termSelectImporterMatcher.findMatch(termSelect, importer))
      .collectFirst {
        case Some(_) => termSelect.name
      }.getOrElse(termSelect)
  }
}

object TermSelectUnqualifier extends TermSelectUnqualifierImpl(TermSelectImporterMatcher)
