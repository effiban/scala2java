package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term

trait TermApplyTypeFunDesugarer extends SameTypeDesugarer[Term.ApplyType]

private[desugarers] class TermApplyTypeFunDesugarerImpl(evaluatedTermSelectQualDesugarer: => EvaluatedTermSelectQualDesugarer,
                                                        evaluatedTermDesugarer: => EvaluatedTermDesugarer)
  extends TermApplyTypeFunDesugarer {

  override def desugar(termApplyType: Term.ApplyType): Term.ApplyType = {
    val desugaredFun = termApplyType.fun match {
      case termName: Term.Name => termName
      case termSelect: Term.Select => evaluatedTermSelectQualDesugarer.desugar(termSelect)
      case aFun => evaluatedTermDesugarer.desugar(aFun)
    }
    termApplyType.copy(fun = desugaredFun)
  }
}
