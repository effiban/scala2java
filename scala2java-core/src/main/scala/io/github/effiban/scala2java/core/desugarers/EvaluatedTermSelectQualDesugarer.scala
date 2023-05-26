package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term

trait EvaluatedTermSelectQualDesugarer extends SameTypeDesugarer[Term.Select]

private[desugarers] class EvaluatedTermSelectQualDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer)
  extends EvaluatedTermSelectQualDesugarer {

  override def desugar(termSelect: Term.Select): Term.Select = {
    termSelect.copy(qual = evaluatedTermDesugarer.desugar(termSelect.qual))
  }
}
