package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermSelectImporterMatcher

import scala.meta.{Importer, Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

trait TermApplyUnqualifier {

  def unqualify(termApply: Term.Apply, importers: List[Importer] = Nil): Term.Apply
}

private[unqualifiers] class TermApplyUnqualifierImpl(termSelectImporterMatcher: TermSelectImporterMatcher) extends TermApplyUnqualifier {

  override def unqualify(termApply: Term.Apply, importers: List[Importer] = Nil): Term.Apply = {
    termApply match {
      case aTermApply@Term.Apply(qualifiedFun: Term.Select, _) =>
        importers.map(importer => termSelectImporterMatcher.findMatch(qualifiedFun, importer))
          .collectFirst {
            // TODO use reflection to handle all relevant cases
            case Some(importer"java.util.List.of") => q"of()"
            case Some(importer"java.util.Optional.empty") => q"empty()"
          }
          .getOrElse(aTermApply)
      case aTermApply => aTermApply
    }
  }
}

object TermApplyUnqualifier extends TermApplyUnqualifierImpl(TermSelectImporterMatcher)
