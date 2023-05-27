package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term

trait AssignDesugarer extends SameTypeDesugarer[Term.Assign]

private[desugarers] class AssignDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends AssignDesugarer {

  override def desugar(assign: Term.Assign): Term.Assign = {
    assign.copy(rhs = evaluatedTermDesugarer.desugar(assign.rhs))
  }
}