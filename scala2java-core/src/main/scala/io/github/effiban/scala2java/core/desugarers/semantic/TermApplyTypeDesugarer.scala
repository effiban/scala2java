package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer

import scala.meta.{Term, XtensionQuasiquoteTerm}


trait TermApplyTypeDesugarer extends DifferentTypeDesugarer[Term.ApplyType, Term]

private[semantic] class TermApplyTypeDesugarerImpl(termApplyDesugarer: => TermApplyDesugarer)
  extends TermApplyTypeDesugarer {

  override def desugar(termApplyType: Term.ApplyType): Term = termApplyType.fun match {
    case q"classOf" => termApplyType
    case _ => termApplyDesugarer.desugar(Term.Apply(termApplyType, Nil))
  }
}
