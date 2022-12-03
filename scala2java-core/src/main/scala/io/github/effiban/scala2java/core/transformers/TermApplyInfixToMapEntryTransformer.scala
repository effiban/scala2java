package io.github.effiban.scala2java.core.transformers

import scala.meta.Term

object TermApplyInfixToMapEntryTransformer extends TermApplyInfixToTermApplyTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Option[Term.Apply] = {
    // Here assuming that the infix has been validated beforehand (for correct op and num of args). Not failing if we don't have to
    val termApply = Term.Apply(
      fun = Term.Select(Term.Name("Map"), Term.Name("entry")),
      args = List(termApplyInfix.lhs) ++ termApplyInfix.args
    )
    Some(termApply)
  }
}
