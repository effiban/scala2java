package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

import scala.meta.Term

trait EvaluatedTermSelectDesugarer extends DifferentTypeDesugarer[Term.Select, Term]

private[desugarers] class EvaluatedTermSelectDesugarerImpl(qualifierTypeInferrer: QualifierTypeInferrer,
                                                           termSelectSupportsNoArgInvocation: TermSelectSupportsNoArgInvocation,
                                                           evaluatedTermSelectQualDesugarer: => EvaluatedTermSelectQualDesugarer)
  extends EvaluatedTermSelectDesugarer {

  override def desugar(termSelect: Term.Select): Term = {
    val maybeQualType = qualifierTypeInferrer.infer(termSelect)
    val context = TermSelectInferenceContext(maybeQualType)
    if (termSelectSupportsNoArgInvocation(termSelect, context)) {
      Term.Apply(termSelect, Nil)
    } else {
      evaluatedTermSelectQualDesugarer.desugar(termSelect)
    }
  }
}
