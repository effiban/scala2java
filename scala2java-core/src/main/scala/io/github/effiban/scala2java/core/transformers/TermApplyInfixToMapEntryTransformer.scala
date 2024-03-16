package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.{Term, XtensionQuasiquoteTerm}

object TermApplyInfixToMapEntryTransformer extends TermApplyInfixToTermApplyTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Option[Term.Apply] = {
    // Here assuming that the infix has been validated beforehand (for correct op and num of args). Not failing if we don't have to
    val termApply = Term.Apply(
      fun = q"java.util.Map.entry",
      args = List(termApplyInfix.lhs) ++ termApplyInfix.args
    )
    Some(termApply)
  }
}
