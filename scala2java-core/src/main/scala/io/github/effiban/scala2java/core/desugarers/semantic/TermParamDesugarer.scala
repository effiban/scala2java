package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Term

trait TermParamDesugarer extends SameTypeDesugarer[Term.Param]

private[semantic] class TermParamDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends TermParamDesugarer {

  override def desugar(termParam: Term.Param): Term.Param = {
    val desugaredDefault = termParam.default.map(evaluatedTermDesugarer.desugar)
    termParam.copy(default = desugaredDefault)
  }
}

