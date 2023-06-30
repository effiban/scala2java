package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.traversers.results.IfTraversalResult

import scala.meta.Lit
import scala.meta.Term.{Block, If}

object TestableIfTraversalResult {

  def apply(`if`: If,
            thenUncertainReturn: Boolean = false,
            elseUncertainReturn: Boolean = false): IfTraversalResult = {
    val cond = `if`.cond
    val thenpResult = `if`.thenp match {
      case thenBlock: Block => TestableBlockTraversalResult(block = thenBlock, uncertainReturn = thenUncertainReturn)
      case aThenp => throw new IllegalArgumentException(
        s"An IfTraversalResult must contain a 'then' clause which is a Block, but the 'then' clause is: $aThenp"
      )
    }
    val maybeElsepResult = `if`.elsep match {
      case elseBlock: Block => Some(TestableBlockTraversalResult(block = elseBlock, uncertainReturn = elseUncertainReturn))
      case Lit.Unit() => None
      case anElsep => throw new IllegalArgumentException(
        s"An IfTraversalResult must contain an either an empty else clause, or an 'else' clause which is a Block - but the 'else' clause is: $anElsep"
      )
    }

    IfTraversalResult(
      cond = cond,
      thenpResult = thenpResult,
      maybeElsepResult = maybeElsepResult
    )
  }
}
