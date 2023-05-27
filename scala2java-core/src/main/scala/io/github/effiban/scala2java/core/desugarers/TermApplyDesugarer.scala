package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term

trait TermApplyDesugarer extends SameTypeDesugarer[Term.Apply]

private[desugarers] class TermApplyDesugarerImpl(termApplyFunDesugarer: => TermApplyFunDesugarer,
                                                 evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends TermApplyDesugarer {

  override def desugar(termApply: Term.Apply): Term.Apply = {
    import termApply._

    val desugaredFunTermApply = termApplyFunDesugarer.desugar(termApply)
    desugaredFunTermApply.copy(args = args.map(evaluatedTermDesugarer.desugar))
  }
}