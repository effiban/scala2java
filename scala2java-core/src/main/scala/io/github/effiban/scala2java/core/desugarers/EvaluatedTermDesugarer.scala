package io.github.effiban.scala2java.core.desugarers

import scala.meta.Term.{ApplyType, Assign, Eta}
import scala.meta.{Term, Transformer, Tree}

trait EvaluatedTermDesugarer extends SameTypeDesugarer[Term]

private[desugarers] class EvaluatedTermDesugarerImpl(evaluatedTermRefDesugarer: => EvaluatedTermRefDesugarer,
                                                     treeDesugarer: => TreeDesugarer) extends EvaluatedTermDesugarer {

  def desugar(term: Term): Term = DesugaringTransformer(term) match {
    case desugaredTerm: Term => desugaredTerm
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Term, but it returned: $desugared")
  }

  private object DesugaringTransformer extends Transformer {

    override def apply(aTree: Tree): Tree =
      aTree match {
        case termRef: Term.Ref => evaluatedTermRefDesugarer.desugar(termRef)
        case apply: Term.Apply => apply // TODO
        case applyType: ApplyType => applyType // TODO
        case applyInfix: Term.ApplyInfix => applyInfix // TODO
        case assign: Assign => assign // TODO
        case eta: Eta => eta // TODO
        case interpolate: Term.Interpolate => interpolate // TODO
        case otherTerm: Term => super.apply(otherTerm)
        case nonTerm: Tree => treeDesugarer.desugar(nonTerm)
      }
  }
}
