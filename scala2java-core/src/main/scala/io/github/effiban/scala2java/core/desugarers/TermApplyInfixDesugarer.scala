package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term

trait TermApplyInfixDesugarer extends SameTypeDesugarer[Term.ApplyInfix]

private[desugarers] class TermApplyInfixDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends TermApplyInfixDesugarer {

  override def desugar(termApplyInfix: Term.ApplyInfix): Term.ApplyInfix = {
    import termApplyInfix._

    val desugaredLhs = evaluatedTermDesugarer.desugar(lhs)
    val desugaredArgs = args.map(evaluatedTermDesugarer.desugar)
    termApplyInfix.copy(lhs = desugaredLhs, args = desugaredArgs)
  }
}