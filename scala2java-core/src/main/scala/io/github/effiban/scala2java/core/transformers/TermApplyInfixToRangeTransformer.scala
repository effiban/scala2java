package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNameValues.{ScalaTo, ScalaUntil}
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.{Term, XtensionQuasiquoteTerm}

object TermApplyInfixToRangeTransformer extends TermApplyInfixToTermApplyTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Option[Term.Apply] = {
    val termApply = (termApplyInfix.op, termApplyInfix.args) match {
      case (Term.Name(ScalaTo), end :: Nil) => inclusiveRangeOf(termApplyInfix.lhs, end)
      case (Term.Name(ScalaTo), _) => handleInvalidRHS(termApplyInfix)
      case (Term.Name(ScalaUntil), end :: Nil) => exclusiveRangeOf(termApplyInfix.lhs, end)
      case (Term.Name(ScalaUntil), _) => handleInvalidRHS(termApplyInfix)
      case _ => handleNonRange(termApplyInfix)
    }
    Some(termApply)
  }

  private def inclusiveRangeOf(start: Term, end: Term) =
    Term.Apply(fun = q"scala.Range.inclusive", args = List(start, end))

  private def exclusiveRangeOf(start: Term, end: Term) =
    Term.Apply(fun = q"scala.Range.apply", args = List(start, end))

  private def handleInvalidRHS(termApplyInfix: Term.ApplyInfix): Term.Apply = {
    throw new IllegalStateException(s"A range must have exactly one RHS term, but ${termApplyInfix.args.length} found in: $termApplyInfix")
  }

  private def handleNonRange(termApplyInfix: Term.ApplyInfix): Term.Apply = {
    throw new IllegalStateException(s"The operator '${termApplyInfix.op}' is not a valid range operator in: $termApplyInfix")
  }
}
