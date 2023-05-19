package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Transformer, Tree}

trait TreeDesugarer extends SameTypeDesugarer[Tree]

private[desugarers] class TreeDesugarerImpl extends TreeDesugarer {

  override def desugar(tree: Tree): Tree = DesugaringTransformer(tree)

  private object DesugaringTransformer extends Transformer {
    override def apply(aTree: Tree): Tree =
      aTree match {
        case aTree => super.apply(aTree)
      }
  }
}
