package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier

import scala.meta.Term

private[predicates] class CoreTermNameHasApplyMethod(termNameClassifier: TermNameClassifier) extends TermNameHasApplyMethod {

  override def apply(termName: Term.Name): Boolean = termNameClassifier.hasApplyMethod(termName)
}

object CoreTermNameHasApplyMethod extends CoreTermNameHasApplyMethod(TermNameClassifier)
