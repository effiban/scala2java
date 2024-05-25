package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Association, Range, TermApplyInfixKind}

import scala.meta.Term

private[syntactic] class CompositeTermApplyInfixDesugarer(classifier: TermApplyInfixClassifier,
                                                          desugarerMap: Map[TermApplyInfixKind, TermApplyInfixDesugarer])
  extends TermApplyInfixDesugarer {

  override def desugar(termApplyInfix: Term.ApplyInfix): Term = {
    val kind = classifier.classify(termApplyInfix)
    desugarerMap.get(kind)
      .map(_.desugar(termApplyInfix))
      .getOrElse(termApplyInfix)
  }
}

object CompositeTermApplyInfixDesugarer extends CompositeTermApplyInfixDesugarer(
  TermApplyInfixClassifier,
  Map(
    Association -> TermApplyInfixToTupleDesugarer,
    Range -> TermApplyInfixToRangeDesugarer
  )
)
