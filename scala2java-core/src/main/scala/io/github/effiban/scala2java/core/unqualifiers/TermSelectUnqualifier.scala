package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermSelectImporterMatcher

import scala.meta.{Importer, Term}

trait TermSelectUnqualifier {

  def unqualify(termSelect: Term.Select, importers: List[Importer] = Nil): Term.Ref
}

private[unqualifiers] class TermSelectUnqualifierImpl(termSelectImporterMatcher: TermSelectImporterMatcher) extends TermSelectUnqualifier {

  override def unqualify(termSelect: Term.Select, importers: List[Importer] = Nil): Term.Ref = {
    importers.map(importer => termSelectImporterMatcher.findMatch(termSelect, importer))
      .collectFirst {
        case Some(_) => termSelect.name
      }.getOrElse(termSelect)
  }
}

object TermSelectUnqualifier extends TermSelectUnqualifierImpl(TermSelectImporterMatcher)
