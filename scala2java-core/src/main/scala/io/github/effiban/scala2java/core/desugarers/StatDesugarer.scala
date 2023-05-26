package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Decl, Defn, Import, Pkg, Stat, Term, Transformer, Tree}

trait StatDesugarer extends SameTypeDesugarer[Stat]

private[desugarers] class StatDesugarerImpl(pkgDesugarer: => PkgDesugarer,
                                            defnDesugarer: => DefnDesugarer,
                                            declDesugarer: => DeclDesugarer,
                                            evaluatedTermDesugarer: => EvaluatedTermDesugarer,
                                            treeDesugarer: => TreeDesugarer) extends StatDesugarer {

  override def desugar(stat: Stat): Stat = DesugaringTransformer(stat) match {
    case desugaredStat: Stat => desugaredStat
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Stat, but it returned: $desugared")
  }

  private object DesugaringTransformer extends Transformer {
    override def apply(aTree: Tree): Tree = aTree match {
      case pkg: Pkg => pkgDesugarer.desugar(pkg)
      case defn: Defn => defnDesugarer.desugar(defn)
      case decl: Decl => declDesugarer.desugar(decl)
      case term: Term => evaluatedTermDesugarer.desugar(term)
      case `import`: Import => `import`
      case otherStat: Stat => super.apply(otherStat)
      case nonStat => treeDesugarer.desugar(nonStat)
    }
  }
}
