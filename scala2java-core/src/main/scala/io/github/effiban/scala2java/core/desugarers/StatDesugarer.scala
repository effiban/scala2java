package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Decl, Defn, Stat, Term}

trait StatDesugarer extends SameTypeDesugarer[Stat]

private[desugarers] class StatDesugarerImpl(defnDesugarer: => DefnDesugarer,
                                            declDesugarer: => DeclDesugarer) extends StatDesugarer {

  override def desugar(stat: Stat): Stat = stat match {
    case defn: Defn => defnDesugarer.desugar(defn)
    case decl: Decl => declDesugarer.desugar(decl)
    case term: Term => term // TODO
    case other => other
  }

}
