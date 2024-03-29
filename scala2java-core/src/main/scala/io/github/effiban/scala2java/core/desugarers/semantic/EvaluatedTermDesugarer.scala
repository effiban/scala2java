package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Term.{ApplyType, Assign, Eta}
import scala.meta.{Term, Transformer, Tree}

trait EvaluatedTermDesugarer extends SameTypeDesugarer[Term]

private[semantic] class EvaluatedTermDesugarerImpl(evaluatedTermRefDesugarer: => EvaluatedTermRefDesugarer,
                                                     termApplyDesugarer: => TermApplyDesugarer,
                                                     termApplyTypeDesugarer: => TermApplyTypeDesugarer,
                                                     termApplyInfixDesugarer: => TermApplyInfixDesugarer,
                                                     assignDesugarer: => AssignDesugarer,
                                                     etaDesugarer: => EtaDesugarer,
                                                     treeDesugarer: => TreeDesugarer) extends EvaluatedTermDesugarer {

  def desugar(term: Term): Term = DesugaringTransformer(term) match {
    case desugaredTerm: Term => desugaredTerm
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Term, but it returned: $desugared")
  }

  private object DesugaringTransformer extends Transformer {

    override def apply(aTree: Tree): Tree =
      aTree match {
        case termRef: Term.Ref => evaluatedTermRefDesugarer.desugar(termRef)
        case apply: Term.Apply => termApplyDesugarer.desugar(apply)
        case applyType: ApplyType => termApplyTypeDesugarer.desugar(applyType)
        case applyInfix: Term.ApplyInfix => termApplyInfixDesugarer.desugar(applyInfix)
        case assign: Assign => assignDesugarer.desugar(assign)
        case eta: Eta => etaDesugarer.desugar(eta)
        case otherTerm: Term => super.apply(otherTerm)
        case nonTerm: Tree => treeDesugarer.desugar(nonTerm)
      }
  }
}
