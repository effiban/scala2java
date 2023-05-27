package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.{Pat, Stat, Term, Transformer, Tree, Type}

trait TreeDesugarer extends SameTypeDesugarer[Tree]

private[semantic] class TreeDesugarerImpl(statDesugarer: => StatDesugarer,
                                            termParamDesugarer: => TermParamDesugarer) extends TreeDesugarer {

  override def desugar(tree: Tree): Tree = DesugaringTransformer(tree)

  private object DesugaringTransformer extends Transformer {

    override def apply(aTree: Tree): Tree =
      aTree match {
        case stat: Stat => statDesugarer.desugar(stat)
        case termParam: Term.Param => termParamDesugarer.desugar(termParam)
        case _: Pat | _: Type => aTree
        case aTree => super.apply(aTree)
      }
  }
}
