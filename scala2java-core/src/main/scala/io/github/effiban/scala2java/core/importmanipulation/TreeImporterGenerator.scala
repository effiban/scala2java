package io.github.effiban.scala2java.core.importmanipulation

import scala.collection.mutable
import scala.meta.{Importer, Pkg, Term, Traverser, Tree, Type}

trait TreeImporterGenerator {
  def generate(tree: Tree): List[Importer]
}

private[importmanipulation] class TreeImporterGeneratorImpl(termApplyImporterGenerator: TermApplyImporterGenerator,
                                                            termSelectImporterGenerator: TermSelectImporterGenerator,
                                                            typeSelectImporterGenerator: TypeSelectImporterGenerator) extends TreeImporterGenerator {

  override def generate(tree: Tree): List[Importer] = {
    val generatingTraverser = new GeneratingTraverser()
    generatingTraverser(tree)
    generatingTraverser.importersBuilder.result()
  }

  private class GeneratingTraverser extends Traverser {

    val importersBuilder: mutable.Builder[Importer, List[Importer]] = List.newBuilder[Importer]

    override def apply(tree: Tree): Unit = tree match {
      case _: Importer | _: Pkg =>
      case termApply: Term.Apply => generateForTermApply(termApply)
      case termSelect: Term.Select => generateForTermSelect(termSelect)
      case typeSelect: Type.Select => importersBuilder ++= typeSelectImporterGenerator.generate(typeSelect)
      case _: Type.Project => // TODO
      case _ => super.apply(tree)
    }

    private def generateForTermApply(termApply: Term.Apply): Unit = {
      termApplyImporterGenerator.generate(termApply) match {
        case Some(importer) =>
          importersBuilder += importer
          super.apply(termApply.args)
        case None => super.apply(termApply)
      }
    }

    private def generateForTermSelect(termSelect: Term.Select): Unit = {
      termSelectImporterGenerator.generate(termSelect) match {
        case Some(importer) => importersBuilder += importer
        case None => super.apply(termSelect)
      }
    }
  }
}

object TreeImporterGenerator extends TreeImporterGeneratorImpl(
  TermApplyImporterGenerator,
  TermSelectImporterGenerator,
  TypeSelectImporterGenerator
)
