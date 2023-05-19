package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Stat, Transformer, Tree}

trait TreeDesugarer extends SameTypeDesugarer[Tree]

private[desugarers] class TreeDesugarerImpl(statDesugarer: => StatDesugarer) extends TreeDesugarer {

  override def desugar(tree: Tree): Tree = DesugaringTransformer(tree)

  private object DesugaringTransformer extends Transformer {

    override def apply(aTree: Tree): Tree =
      aTree match {
        case stat: Stat => statDesugarer.desugar(stat)
        case aTree => super.apply(aTree)
      }
  }
}
