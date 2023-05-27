package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaClassOf

import scala.meta.Term

trait TermApplyTypeDesugarer extends DifferentTypeDesugarer[Term.ApplyType, Term]

private[desugarers] class TermApplyTypeDesugarerImpl(termApplyTypeFunDesugarer: => TermApplyTypeFunDesugarer)
  extends TermApplyTypeDesugarer {

  override def desugar(termApplyType: Term.ApplyType): Term = termApplyType.fun match {
    case Term.Name(ScalaClassOf) => termApplyType
    case _ =>
      val desugaredTermApplyType = termApplyTypeFunDesugarer.desugar(termApplyType)
      Term.Apply(desugaredTermApplyType, Nil)
  }
}
