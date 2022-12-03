package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Association, Operator, Range, TermApplyInfixKind, Unclassified}

import scala.meta.Term

private[transformers] class CompositeTermApplyInfixToTermApplyTransformer(classifier: TermApplyInfixClassifier,
                                                                          transformerMap: Map[TermApplyInfixKind, TermApplyInfixToTermApplyTransformer])
  extends TermApplyInfixToTermApplyTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Option[Term.Apply] = {
    val kind = classifier.classify(termApplyInfix)
    transformerFor(kind).transform(termApplyInfix)
  }

  private def transformerFor(kind: TermApplyInfixKind) = {
    transformerMap.getOrElse(kind, throw new IllegalStateException(s"No TermApplyInfixToTermApplyTransformer defined for kind $kind"))
  }
}

object CompositeTermApplyInfixToTermApplyTransformer extends CompositeTermApplyInfixToTermApplyTransformer(
  TermApplyInfixClassifier,
  Map(
    Association -> TermApplyInfixToMapEntryTransformer,
    Range -> TermApplyInfixToRangeTransformer,
    Operator -> TermApplyInfixToTermApplyTransformer.Empty,
    Unclassified -> BasicTermApplyInfixToTermApplyTransformer
  )
)

