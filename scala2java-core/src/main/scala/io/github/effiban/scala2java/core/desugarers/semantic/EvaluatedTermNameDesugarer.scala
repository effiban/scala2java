package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer
import io.github.effiban.scala2java.spi.predicates.TermNameSupportsNoArgInvocation

import scala.meta.Term

trait EvaluatedTermNameDesugarer extends DifferentTypeDesugarer[Term.Name, Term]

private[semantic] class EvaluatedTermNameDesugarerImpl(termNameSupportsNoArgInvocation: TermNameSupportsNoArgInvocation)
  extends DifferentTypeDesugarer[Term.Name, Term] with EvaluatedTermNameDesugarer {

  override def desugar(termName: Term.Name): Term = {
    if (termNameSupportsNoArgInvocation(termName)) Term.Apply(termName, Nil) else termName
  }
}
