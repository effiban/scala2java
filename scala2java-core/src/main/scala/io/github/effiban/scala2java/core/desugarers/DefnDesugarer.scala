package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Defn, Transformer, Tree}

trait DefnDesugarer extends SameTypeDesugarer[Defn]

private[desugarers] class DefnDesugarerImpl(defnDefDesugarer: => DefnDefDesugarer,
                                            defnObjectDesugarer: => DefnObjectDesugarer,
                                            treeDesugarer: => TreeDesugarer) extends DefnDesugarer {

  override def desugar(defn: Defn): Defn = DesugaringTransformer(defn) match {
    case desugaredDefn: Defn => desugaredDefn
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Defn, but it returned: $desugared")
  }

  private object DesugaringTransformer extends Transformer {

    override def apply(aTree: Tree): Tree = {
      aTree match {
        case defnDef: Defn.Def => defnDefDesugarer.desugar(defnDef)
        case objectDef: Defn.Object => defnObjectDesugarer.desugar(objectDef)
        case otherDefn: Defn => super.apply(otherDefn)
        case nonDefn => treeDesugarer.desugar(nonDefn)
      }
    }
  }

}
