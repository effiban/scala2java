package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importer, Term, XtensionQuasiquoteTerm}

trait TermApplyImporterGenerator {
  def generate(termApply: Term.Apply): Option[Importer]
}

private[importmanipulation] class TermApplyImporterGeneratorImpl(qualifiedNameImporterGenerator: QualifiedNameImporterGenerator)
  extends TermApplyImporterGenerator {

  override def generate(termApply: Term.Apply): Option[Importer] = (termApply.fun, termApply.args) match {
    case (Term.Select(q"scala.Array", _), _) => None
    case (Term.Select(qual: Term.Ref, name), args) if qual.isPath => generateForStaticMethod(qual, name, args)

    case _ => None
  }

  private def generateForStaticMethod(qual: Term.Ref, name: Term.Name, args: List[Term]) =
    qualifiedNameImporterGenerator.generateForStaticMethod(qual, name.value, args)
}

object TermApplyImporterGenerator extends TermApplyImporterGeneratorImpl(QualifiedNameImporterGenerator)

