package effiban.scala2java.classifiers

import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term

class TermApplyInfixClassifierTest extends UnitTestSuite {

  private val OperatorToExpectedResultMappings = Table(
    ("Operator", "ExpectedResult"),
    ("to", true),
    ("until", true),
    ("+", false),
    ("*", false)
  )

  forAll(OperatorToExpectedResultMappings) { (operator: String, expectedResult: Boolean) =>
    test(s"isRange() for '$operator' should return $expectedResult") {
      val termApplyInfix = termApplyInfixWithOp(operator)
      TermApplyInfixClassifier.isRange(termApplyInfix) shouldBe expectedResult
    }
  }

  private def termApplyInfixWithOp(operator: String) = {
    Term.ApplyInfix(
      op = Term.Name(operator),
      lhs = Term.Name("x"),
      targs = Nil,
      args = List(Term.Name("y"))
    )
  }
}
