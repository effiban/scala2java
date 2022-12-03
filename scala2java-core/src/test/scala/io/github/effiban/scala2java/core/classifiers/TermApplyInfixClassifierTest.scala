package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.TermApplyInfixClassifier.classify
import io.github.effiban.scala2java.core.entities.TermApplyInfixKind.{Association, Operator, Range, TermApplyInfixKind, Unclassified}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.{And, BitwiseAnd, BitwiseOr, BitwiseXor, Divide, Equals, GreaterEquals, GreaterThan, LessEquals, LessThan, Minus, Modulus, Multiply, NotEquals, Or, Plus, ScalaAssociation, ScalaTo, ScalaUntil}

import scala.meta.Term

class TermApplyInfixClassifierTest extends UnitTestSuite {

  private val OperatorToExpectedKindMapping = Table(
    ("Operator", "ExpectedKind"),
    (ScalaAssociation, Association),
    (ScalaTo, Range),
    (ScalaUntil, Range),
    (Plus, Operator),
    (Minus, Operator),
    (Multiply, Operator),
    (Divide, Operator),
    (Modulus, Operator),
    (And, Operator),
    (Or, Operator),
    (BitwiseAnd, Operator),
    (BitwiseOr, Operator),
    (BitwiseXor, Operator),
    (Equals, Operator),
    (NotEquals, Operator),
    (GreaterThan, Operator),
    (GreaterEquals, Operator),
    (LessThan, Operator),
    (LessEquals, Operator),
    (Term.Name("!~"), Unclassified),
    (Term.Name("foo"), Unclassified)
  )

  forAll(OperatorToExpectedKindMapping) { (operator: Term.Name, expectedKind: TermApplyInfixKind) =>
    test(s"classify() for '$operator' should return $expectedKind") {
      val termApplyInfix = termApplyInfixWithOp(operator)
      classify(termApplyInfix) shouldBe expectedKind
    }
  }

  private def termApplyInfixWithOp(operator: Term.Name) = {
    Term.ApplyInfix(
      op = operator,
      lhs = Term.Name("x"),
      targs = Nil,
      args = List(Term.Name("y"))
    )
  }
}
