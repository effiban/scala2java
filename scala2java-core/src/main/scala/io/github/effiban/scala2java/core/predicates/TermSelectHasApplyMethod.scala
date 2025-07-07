package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup

import scala.meta.{Term, XtensionQuasiquoteTerm}

// TODO - once file-scope or external type inference is added, add ability to check by a given type as well
trait TermSelectHasApplyMethod extends (Term.Select => Boolean)

private[predicates] class TermSelectHasApplyMethodImpl(scalaReflectionLookup: ScalaReflectionLookup) extends TermSelectHasApplyMethod {
  override def apply(termSelect: Term.Select): Boolean = scalaReflectionLookup.isTermMemberOf(termSelect, q"apply")
}

object TermSelectHasApplyMethod extends TermSelectHasApplyMethodImpl(ScalaReflectionLookup)
