package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer

import scala.meta.{Term, Transformer, Tree}

trait EvaluatedTermRefDesugarer extends DifferentTypeDesugarer[Term.Ref, Term]

private[semantic] class EvaluatedTermRefDesugarerImpl(evaluatedTermSelectDesugarer: => EvaluatedTermSelectDesugarer,
                                                      applyUnaryDesugarer: => ApplyUnaryDesugarer,
                                                      treeDesugarer: => TreeDesugarer) extends EvaluatedTermRefDesugarer {
  override def desugar(termRef: Term.Ref): Term = DesugaringTransformer(termRef) match {
    case desugaredTerm: Term => desugaredTerm
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Term, but it returned: $desugared")
  }

  private object DesugaringTransformer extends Transformer {

    override def apply(aTree: Tree): Tree =
      aTree match {
        case termName: Term.Name => termName
        case termSelect: Term.Select => evaluatedTermSelectDesugarer.desugar(termSelect)
        case termThis: Term.This => termThis // TODO
        case termSuper: Term.Super => termSuper // TODO
        case applyUnary: Term.ApplyUnary => applyUnaryDesugarer.desugar(applyUnary)
        case otherTermRef: Term.Ref => super.apply(otherTermRef)
        case nonTermRef => treeDesugarer.desugar(nonTermRef)
      }
  }
}