package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term

trait ApplyUnaryDesugarer extends SameTypeDesugarer[Term.ApplyUnary]

private[desugarers] class ApplyUnaryDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends ApplyUnaryDesugarer {
  override def desugar(applyUnary: Term.ApplyUnary): Term.ApplyUnary = {
    applyUnary.copy(arg = evaluatedTermDesugarer.desugar(applyUnary.arg))
  }
}
