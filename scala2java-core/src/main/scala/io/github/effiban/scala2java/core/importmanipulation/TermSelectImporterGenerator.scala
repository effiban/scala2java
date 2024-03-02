package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importer, Term, XtensionQuasiquoteTerm}

trait TermSelectImporterGenerator {
  def generate(termSelect: Term.Select): Option[Importer]
}

private[importmanipulation] class TermSelectImporterGeneratorImpl(qualifiedNameImporterGenerator: QualifiedNameImporterGenerator)
  extends TermSelectImporterGenerator {

  override def generate(termSelect: Term.Select): Option[Importer] = termSelect.parent match {
    case Some(_: Term.Apply) | Some(_: Term.ApplyType) => None
    case _ => generateInner(termSelect)
  }

  private def generateInner(termSelect: Term.Select): Option[Importer] = termSelect match {
    case q"scala.Array" => None
    case Term.Select(qual: Term.Ref, name) if qual.isPath => generateForStaticField(qual, name).orElse(generateForType(qual, name))
    case _ => None
  }

  private def generateForStaticField(qual: Term.Ref, name: Term.Name): Option[Importer] =
    qualifiedNameImporterGenerator.generateForStaticField(qual, name.value)

  private def generateForType(qual: Term.Ref, name: Term.Name): Option[Importer] =
    qualifiedNameImporterGenerator.generateForType(qual, name.value)
}

object TermSelectImporterGenerator extends TermSelectImporterGeneratorImpl(QualifiedNameImporterGenerator)

