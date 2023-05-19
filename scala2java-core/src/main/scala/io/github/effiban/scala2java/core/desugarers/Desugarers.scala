package io.github.effiban.scala2java.core.desugarers

import scala.meta.Source

class Desugarers() {

  val sourceDesugarer: SameTypeDesugarer[Source] = new DefaultSameTypeDesugarer[Source](treeDesugarer)

  private lazy val treeDesugarer: TreeDesugarer = new TreeDesugarerImpl()
}
