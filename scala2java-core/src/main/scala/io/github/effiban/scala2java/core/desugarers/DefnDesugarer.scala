package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Defn, Transformer, Tree}

trait DefnDesugarer extends SameTypeDesugarer[Defn]

private[desugarers] class DefnDesugarerImpl(treeDesugarer: => TreeDesugarer) extends DefnDesugarer {

  override def desugar(defn: Defn): Defn = DesugaringTransformer(defn) match {
    case desugaredDefn: Defn => desugaredDefn
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Defn, but it returned: $desugared")
  }

  private object DesugaringTransformer extends Transformer {

    override def apply(aTree: Tree): Tree = {
      aTree match {
        case defDef: Defn.Def => defDef // TODO
        case objectDef: Defn.Object => objectDef // TODO
        case otherDefn: Defn => super.apply(otherDefn)
        case nonDefn => treeDesugarer.desugar(nonDefn)
      }
    }
  }

}
