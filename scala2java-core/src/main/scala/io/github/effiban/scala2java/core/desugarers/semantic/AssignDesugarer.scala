package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Term

trait AssignDesugarer extends SameTypeDesugarer[Term.Assign]

private[semantic] class AssignDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends AssignDesugarer {

  override def desugar(assign: Term.Assign): Term.Assign = {
    assign.copy(rhs = evaluatedTermDesugarer.desugar(assign.rhs))
  }
}