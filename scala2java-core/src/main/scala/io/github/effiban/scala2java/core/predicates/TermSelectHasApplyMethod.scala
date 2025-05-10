package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.isTermMemberOf

import scala.meta.{Term, XtensionQuasiquoteTerm}

// TODO - once file-scope or external type inference is added, add ability to check by a given type as well
trait TermSelectHasApplyMethod extends (Term.Select => Boolean)

object TermSelectHasApplyMethod extends TermSelectHasApplyMethod {

  override def apply(termSelect: Term.Select): Boolean = {
    (termSelect.qual, termSelect.name) match {
      case (qual: Term.Ref, name) =>
        hasApplyMethodOnObjectOf(termSelect)
      case _ => false
    }
  }

  private def hasApplyMethodOnObjectOf(termSelect: Term.Select) = {
    isTermMemberOf(termSelect, q"apply")
  }
}
