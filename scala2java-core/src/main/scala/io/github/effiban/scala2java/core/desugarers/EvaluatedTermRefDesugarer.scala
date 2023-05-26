package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Term, Transformer, Tree}

trait EvaluatedTermRefDesugarer extends DifferentTypeDesugarer[Term.Ref, Term]

private[desugarers] class EvaluatedTermRefDesugarerImpl(evaluatedTermNameDesugarer: => EvaluatedTermNameDesugarer,
                                                        evaluatedTermSelectDesugarer: => EvaluatedTermSelectDesugarer,
                                                        treeDesugarer: => TreeDesugarer) extends EvaluatedTermRefDesugarer {
  override def desugar(termRef: Term.Ref): Term = DesugaringTransformer(termRef) match {
    case desugaredTerm: Term => desugaredTerm
    case desugared => throw new IllegalStateException(s"The inner transformer should return a Term, but it returned: $desugared")
  }

  private object DesugaringTransformer extends Transformer {

    override def apply(aTree: Tree): Tree =
      aTree match {
        case termName: Term.Name => evaluatedTermNameDesugarer.desugar(termName)
        case termSelect: Term.Select => evaluatedTermSelectDesugarer.desugar(termSelect)
        case termThis: Term.This => termThis // TODO
        case termSuper: Term.Super => termSuper // TODO
        case applyUnary: Term.ApplyUnary => applyUnary // TODO
        case otherTermRef: Term.Ref => super.apply(otherTermRef)
        case nonTermRef => treeDesugarer.desugar(nonTermRef)
      }
  }
}