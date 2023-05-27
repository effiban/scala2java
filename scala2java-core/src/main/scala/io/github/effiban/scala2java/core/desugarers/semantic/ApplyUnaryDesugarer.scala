package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Term

trait ApplyUnaryDesugarer extends SameTypeDesugarer[Term.ApplyUnary]

private[semantic] class ApplyUnaryDesugarerImpl(evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends ApplyUnaryDesugarer {
  override def desugar(applyUnary: Term.ApplyUnary): Term.ApplyUnary = {
    applyUnary.copy(arg = evaluatedTermDesugarer.desugar(applyUnary.arg))
  }
}
