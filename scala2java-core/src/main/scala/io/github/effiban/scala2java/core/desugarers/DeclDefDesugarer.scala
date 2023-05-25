package io.github.effiban.scala2java.core.desugarers

import scala.meta.Decl

trait DeclDefDesugarer extends SameTypeDesugarer[Decl.Def]

private[desugarers] class DeclDefDesugarerImpl(termParamDesugarer: => TermParamDesugarer) extends DeclDefDesugarer {

  override def desugar(declDef: Decl.Def): Decl.Def = {
    val desugaredParamss = declDef.paramss.map(_.map(termParamDesugarer.desugar))
    declDef.copy(paramss = desugaredParamss)
  }
}
