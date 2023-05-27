package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer
import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaClassOf

import scala.meta.Term

trait TermApplyTypeDesugarer extends DifferentTypeDesugarer[Term.ApplyType, Term]

private[semantic] class TermApplyTypeDesugarerImpl(termApplyDesugarer: => TermApplyDesugarer)
  extends TermApplyTypeDesugarer {

  override def desugar(termApplyType: Term.ApplyType): Term = termApplyType.fun match {
    case Term.Name(ScalaClassOf) => termApplyType
    case _ => termApplyDesugarer.desugar(Term.Apply(termApplyType, Nil))
  }
}
