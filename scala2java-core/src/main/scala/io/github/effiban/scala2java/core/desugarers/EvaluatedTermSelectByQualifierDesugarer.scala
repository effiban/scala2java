package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term

trait EvaluatedTermSelectByQualifierDesugarer extends SameTypeDesugarer[Term.Select]

private[desugarers] class EvaluatedTermSelectByQualifierDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer)
  extends EvaluatedTermSelectByQualifierDesugarer {

  override def desugar(termSelect: Term.Select): Term.Select = {
    termSelect.copy(qual = evaluatedTermDesugarer.desugar(termSelect.qual))
  }
}
