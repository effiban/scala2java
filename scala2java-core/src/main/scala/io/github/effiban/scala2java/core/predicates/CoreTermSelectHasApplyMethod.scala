package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.TermSelectClassifier
import io.github.effiban.scala2java.spi.predicates.TermSelectHasApplyMethod

import scala.meta.Term

private[predicates] class CoreTermSelectHasApplyMethod(termSelectClassifier: TermSelectClassifier) extends TermSelectHasApplyMethod {

  override def apply(termSelect: Term.Select): Boolean = termSelectClassifier.hasApplyMethod(termSelect)
}

object CoreTermSelectHasApplyMethod extends CoreTermSelectHasApplyMethod(TermSelectClassifier)
