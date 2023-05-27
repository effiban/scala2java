package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer
import io.github.effiban.scala2java.core.typeinference.QualifierTypeInferrer
import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

import scala.meta.Term

trait EvaluatedTermSelectDesugarer extends DifferentTypeDesugarer[Term.Select, Term]

private[semantic] class EvaluatedTermSelectDesugarerImpl(qualifierTypeInferrer: QualifierTypeInferrer,
                                                           termSelectSupportsNoArgInvocation: TermSelectSupportsNoArgInvocation,
                                                           evaluatedTermSelectQualDesugarer: => EvaluatedTermSelectQualDesugarer)
  extends EvaluatedTermSelectDesugarer {

  override def desugar(termSelect: Term.Select): Term = {
    val desugaredTermSelect = evaluatedTermSelectQualDesugarer.desugar(termSelect)
    val maybeQualType = qualifierTypeInferrer.infer(desugaredTermSelect)
    val context = TermSelectInferenceContext(maybeQualType)
    if (termSelectSupportsNoArgInvocation(desugaredTermSelect, context)) {
      Term.Apply(desugaredTermSelect, Nil)
    } else {
      desugaredTermSelect
    }
  }
}
