package io.github.effiban.scala2java.core.desugarers

import scala.meta.Source

class Desugarers() {

  private lazy val defnDesugarer: DefnDesugarer = new DefnDesugarerImpl()

  val sourceDesugarer: SameTypeDesugarer[Source] = new DefaultSameTypeDesugarer[Source](treeDesugarer)

  private lazy val statDesugarer: StatDesugarer = new StatDesugarerImpl(defnDesugarer)

  private lazy val treeDesugarer: TreeDesugarer = new TreeDesugarerImpl(statDesugarer)
}
