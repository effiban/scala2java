package effiban.scala2java.classifiers

import effiban.scala2java.classifiers.TermApplyInfixClassifier.{isAssociation, isRange}
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term

class TermApplyInfixClassifierTest extends UnitTestSuite {

  private val OperatorToExpectedIsRangeMappings = Table(
    ("Operator", "ExpectedIsRange"),
    ("to", true),
    ("until", true),
    ("+", false),
    ("*", false)
  )

  private val OperatorToExpectedIsAssociationMappings = Table(
    ("Operator", "ExpectedIsAssociation"),
    ("->", true),
    ("+", false),
    ("to", false)
  )

  forAll(OperatorToExpectedIsRangeMappings) { (operator: String, expectedResult: Boolean) =>
    test(s"isRange() for '$operator' should return $expectedResult") {
      val termApplyInfix = termApplyInfixWithOp(operator)
      isRange(termApplyInfix) shouldBe expectedResult
    }
  }

  forAll(OperatorToExpectedIsAssociationMappings) { (operator: String, expectedResult: Boolean) =>
    test(s"isAssociation() for '$operator' should return $expectedResult") {
      val termApplyInfix = termApplyInfixWithOp(operator)
      isAssociation(termApplyInfix) shouldBe expectedResult
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
