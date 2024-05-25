package io.github.effiban.scala2java.core.desugarers.syntactic

import scala.meta.{Term, XtensionQuasiquoteTerm}

object TermApplyInfixToRangeDesugarer extends TermApplyInfixDesugarer {

  override def desugar(termApplyInfix: Term.ApplyInfix): Term = {
    (termApplyInfix.op, termApplyInfix.args) match {
      case (q"to", end :: Nil) => inclusiveRangeOf(termApplyInfix.lhs, end)
      case (q"to", _) => handleInvalidRHS(termApplyInfix)
      case (q"until", end :: Nil) => exclusiveRangeOf(termApplyInfix.lhs, end)
      case (q"until", _) => handleInvalidRHS(termApplyInfix)
      case _ => handleNonRange(termApplyInfix)
    }
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
