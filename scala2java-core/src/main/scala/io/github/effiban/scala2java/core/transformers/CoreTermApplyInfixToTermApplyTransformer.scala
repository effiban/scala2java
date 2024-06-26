package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Association, Operator, Range, TermApplyInfixKind, Unclassified}
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.Term

private[transformers] class CoreTermApplyInfixToTermApplyTransformer(classifier: TermApplyInfixClassifier,
                                                                     transformerMap: Map[TermApplyInfixKind, TermApplyInfixToTermApplyTransformer])
  extends TermApplyInfixToTermApplyTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Option[Term.Apply] = {
    val kind = classifier.classify(termApplyInfix)
    transformerMap.get(kind)
      .flatMap(_.transform(termApplyInfix))
  }
}

object CoreTermApplyInfixToTermApplyTransformer extends CoreTermApplyInfixToTermApplyTransformer(
  TermApplyInfixClassifier,
  Map(
    Operator -> TermApplyInfixToTermApplyTransformer.Empty,
    Unclassified -> BasicTermApplyInfixToTermApplyTransformer
  )
)

