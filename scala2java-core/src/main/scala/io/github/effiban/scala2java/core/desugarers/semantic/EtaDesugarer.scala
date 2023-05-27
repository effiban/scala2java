package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Term

trait EtaDesugarer extends SameTypeDesugarer[Term.Eta]

private[semantic] class EtaDesugarerImpl(evaluatedTermSelectQualDesugarer: => EvaluatedTermSelectQualDesugarer,
                                           termApplyTypeFunDesugarer: => TermApplyTypeFunDesugarer,
                                           evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends EtaDesugarer {

  override def desugar(eta: Term.Eta): Term.Eta = {
    val desugaredExpr = eta.expr match {
      case name: Term.Name => name
      case select: Term.Select => evaluatedTermSelectQualDesugarer.desugar(select)
      case applyType: Term.ApplyType => termApplyTypeFunDesugarer.desugar(applyType)
      case expr => evaluatedTermDesugarer.desugar(expr)
    }
    eta.copy(expr = desugaredExpr)
  }
}