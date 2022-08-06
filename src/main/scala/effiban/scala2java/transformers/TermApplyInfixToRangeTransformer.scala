package effiban.scala2java.transformers

import effiban.scala2java.entities.ScalaOperatorName.{To, Until}

import scala.meta.Term
import scala.meta.Term.Select

trait TermApplyInfixToRangeTransformer {
  def transform(termApplyInfix: Term.ApplyInfix): Term.Apply
}

object TermApplyInfixToRangeTransformer extends TermApplyInfixToRangeTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Term.Apply = {
    (termApplyInfix.op, termApplyInfix.args) match {
      case (Term.Name(To), end :: Nil) => inclusiveRangeOf(termApplyInfix.lhs, end)
      case (Term.Name(To), _) => handleInvalidRHS(termApplyInfix)
      case (Term.Name(Until), end :: Nil) => exclusiveRangeOf(termApplyInfix.lhs, end)
      case (Term.Name(Until), _) => handleInvalidRHS(termApplyInfix)
      case _ => handleNonRange(termApplyInfix)
    }
  }

  private def inclusiveRangeOf(start: Term, end: Term) =
    Term.Apply(fun = Select(Term.Name("Range"), Term.Name("inclusive")), args = List(start, end))

  private def exclusiveRangeOf(start: Term, end: Term) =
    Term.Apply(fun = Term.Name("Range"), args = List(start, end))

  private def handleInvalidRHS(termApplyInfix: Term.ApplyInfix): Term.Apply = {
    throw new IllegalStateException(s"A range must have exactly one RHS term, but ${termApplyInfix.args.length} found in: $termApplyInfix")
  }

  private def handleNonRange(termApplyInfix: Term.ApplyInfix): Term.Apply = {
    throw new IllegalStateException(s"The operator '${termApplyInfix.op}' is not a valid range operator in: $termApplyInfix")
  }
}
