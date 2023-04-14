package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.entities.TermNameValues.{Apply, Empty}
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

import scala.meta.{Term, XtensionQuasiquoteTerm}

private[predicates] class CoreTermSelectSupportsNoArgInvocation(termNameClassifier: TermNameClassifier)
  extends TermSelectSupportsNoArgInvocation {

  override def apply(termSelect: Term.Select, context: TermSelectInferenceContext = TermSelectInferenceContext()): Boolean = {
    (termSelect.qual, termSelect.name) match {
      case (termName: Term.Name, Term.Name(Apply)) if termNameClassifier.hasApplyMethod(termName) => true
      case (termName: Term.Name, Term.Name(Empty)) if termNameClassifier.hasEmptyMethod(termName) => true
      case (_, q"toString") => true
      case _ => false
    }
  }
}

object CoreTermSelectSupportsNoArgInvocation extends CoreTermSelectSupportsNoArgInvocation(TermNameClassifier)
