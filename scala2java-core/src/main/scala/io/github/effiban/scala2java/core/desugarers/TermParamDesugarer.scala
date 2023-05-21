package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term

trait TermParamDesugarer extends SameTypeDesugarer[Term.Param]

private[desugarers] class TermParamDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends TermParamDesugarer {

  override def desugar(termParam: Term.Param): Term.Param = {
    val desugaredDefault = termParam.default.map(evaluatedTermDesugarer.desugar)
    termParam.copy(default = desugaredDefault)
  }
}

