package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier

import scala.meta.Term

private[predicates] class CoreTermNameSupportsNoArgInvocation(termNameClassifier: TermNameClassifier) extends TermNameSupportsNoArgInvocation {

  override def apply(termName: Term.Name): Boolean = termNameClassifier.supportsNoArgInvocation(termName)
}

object CoreTermNameSupportsNoArgInvocation extends CoreTermNameSupportsNoArgInvocation(TermNameClassifier)
