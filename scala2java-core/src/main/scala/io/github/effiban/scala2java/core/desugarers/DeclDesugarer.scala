package io.github.effiban.scala2java.core.desugarers

import scala.meta.Decl

trait DeclDesugarer extends SameTypeDesugarer[Decl]

private[desugarers] class DeclDesugarerImpl(declDefDesugarer: => DeclDefDesugarer) extends DeclDesugarer {

  override def desugar(decl: Decl): Decl = decl match {
    case declDef: Decl.Def => declDefDesugarer.desugar(declDef)
    case other => other
  }

}
