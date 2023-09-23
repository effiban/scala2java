package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importer, Tree}

trait TreeImporterUsed extends ((Tree, Importer) => Boolean)
private[importmanipulation] class TreeImporterUsedImpl extends TreeImporterUsed {

  override def apply(tree: Tree, importer: Importer): Boolean = {
    false //TODO
  }
}

object TreeImporterUsed extends TreeImporterUsedImpl
