package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importer, Tree, Type}

trait TreeImporterUsed extends ((Tree, Importer) => Boolean)
private[importmanipulation] class TreeImporterUsedImpl(typeNameImporterMatcher: TypeNameImporterMatcher)
  extends TreeImporterUsed {

  override def apply(tree: Tree, importer: Importer): Boolean = {
    tree.collect {
      case typeName: Type.Name => typeNameImporterMatcher.findMatch(typeName, importer).nonEmpty
        // TODO support Type.Select when matches by prefix of qualifier
    }.nonEmpty
  }
}

object TreeImporterUsed extends TreeImporterUsedImpl(TypeNameImporterMatcher)
