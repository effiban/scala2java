package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importer, Tree, Type}

trait TreeImporterGenerator {
  def generate(tree: Tree): List[Importer]
}

private[importmanipulation] class TreeImporterGeneratorImpl(typeSelectImporterGenerator: TypeSelectImporterGenerator) extends TreeImporterGenerator {

  override def generate(tree: Tree): List[Importer] = {
    tree.collect {
      case typeSelect: Type.Select => typeSelectImporterGenerator.generate(typeSelect)
      // TODO generate for Type.Project
      // TODO generate for Term.Select-s when relevant, using semantic information
    }.flatten
  }
}

object TreeImporterGenerator extends TreeImporterGeneratorImpl(TypeSelectImporterGenerator)
