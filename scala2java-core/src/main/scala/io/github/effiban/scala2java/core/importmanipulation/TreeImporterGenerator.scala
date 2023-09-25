package io.github.effiban.scala2java.core.importmanipulation

import scala.collection.mutable
import scala.meta.{Importer, Term, Traverser, Tree, Type}

trait TreeImporterGenerator {
  def generate(tree: Tree): List[Importer]
}

private[importmanipulation] class TreeImporterGeneratorImpl(termApplyImporterGenerator: TermApplyImporterGenerator,
                                                            typeSelectImporterGenerator: TypeSelectImporterGenerator) extends TreeImporterGenerator {

  override def generate(tree: Tree): List[Importer] = {
    val generatingTraverser = new GeneratingTraverser()
    generatingTraverser(tree)
    generatingTraverser.importersBuilder.result()
  }

  private class GeneratingTraverser extends Traverser {

    val importersBuilder: mutable.Builder[Importer, List[Importer]] = List.newBuilder[Importer]

    override def apply(tree: Tree): Unit = tree match {
      case termApply: Term.Apply => importersBuilder ++= termApplyImporterGenerator.generate(termApply)
      case _: Term.Select => // TODO handle standalone qualified names when relevant, with reflection
      case typeSelect: Type.Select => importersBuilder ++= typeSelectImporterGenerator.generate(typeSelect)
      case _: Type.Project => // TODO
      case _ => super.apply(tree)
    }
  }
}

object TreeImporterGenerator extends TreeImporterGeneratorImpl(
  TermApplyImporterGenerator,
  TypeSelectImporterGenerator
)
