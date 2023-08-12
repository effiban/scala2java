package io.github.effiban.scala2java.core.importadders

import scala.meta.{Importer, Tree, Type}

trait TreeImporterGenerator {
  def generate(tree: Tree): List[Importer]
}

private[importadders] class TreeImporterGeneratorImpl(typeSelectImporterGenerator: TypeSelectImporterGenerator) extends TreeImporterGenerator {

  override def generate(tree: Tree): List[Importer] = {
    tree.collect {
      case typeSelect: Type.Select => typeSelectImporterGenerator.generate(typeSelect)
      // TODO generate for Term.Select-s when relevant, using semantic information
    }
  }
}

object TreeImporterGenerator extends TreeImporterGeneratorImpl(TypeSelectImporterGenerator)
