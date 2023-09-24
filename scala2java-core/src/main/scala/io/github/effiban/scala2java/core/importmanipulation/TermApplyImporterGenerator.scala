package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importer, Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

trait TermApplyImporterGenerator {
  def generate(termApply: Term.Apply): Option[Importer]
}

object TermApplyImporterGenerator extends TermApplyImporterGenerator {

  override def generate(termApply: Term.Apply): Option[Importer] = termApply match {
    // TODO generate for all relevant cases using reflection, once qualified terms are fully supported in the flow
    case q"java.util.List.of()" => Some(importer"java.util.List.of")
    case q"java.util.Optional.empty()" => Some(importer"java.util.Optional.empty")
    case _ => None
  }
}
