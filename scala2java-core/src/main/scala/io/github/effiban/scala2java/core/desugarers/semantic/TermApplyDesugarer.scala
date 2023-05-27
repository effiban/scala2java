package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Term

trait TermApplyDesugarer extends SameTypeDesugarer[Term.Apply]

private[semantic] class TermApplyDesugarerImpl(termApplyFunDesugarer: => TermApplyFunDesugarer,
                                                 evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends TermApplyDesugarer {

  override def desugar(termApply: Term.Apply): Term.Apply = {
    import termApply._

    val desugaredFunTermApply = termApplyFunDesugarer.desugar(termApply)
    desugaredFunTermApply.copy(args = args.map(evaluatedTermDesugarer.desugar))
  }
}