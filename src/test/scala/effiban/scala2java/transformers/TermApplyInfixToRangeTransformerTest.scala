package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames.{PlusTermName, ScalaInclusiveTermName, ScalaRangeTermName, ScalaToTermName, ScalaUntilTermName}

import scala.meta.{Lit, Term}

class TermApplyInfixToRangeTransformerTest extends UnitTestSuite {

  test("transform 'to' with one arg should return a Range method call") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(1),
      op = ScalaToTermName,
      targs = Nil,
      args = List(Lit.Int(10))
    )

    val expectedTermApply = Term.Apply(
      fun = Term.Select(ScalaRangeTermName, ScalaInclusiveTermName),
      args = List(Lit.Int(1), Lit.Int(10))
    )

    TermApplyInfixToRangeTransformer.transform(termApplyInfix).structure shouldBe expectedTermApply.structure
  }

  test("transform 'to' with no args should throw an IllegalStateException") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(1),
      op = ScalaToTermName,
      targs = Nil,
      args = Nil
    )

    intercept[IllegalStateException] {
      TermApplyInfixToRangeTransformer.transform(termApplyInfix).structure
    }
  }

  test("transform 'until' with one arg") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(0),
      op = ScalaUntilTermName,
      targs = Nil,
      args = List(Lit.Int(10))
    )

    val expectedTermApply = Term.Apply(
      fun = ScalaRangeTermName,
      args = List(Lit.Int(0), Lit.Int(10))
    )

    TermApplyInfixToRangeTransformer.transform(termApplyInfix).structure shouldBe expectedTermApply.structure
  }

  test("transform 'until' with two args should throw an IllegalStateException") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(0),
      op = ScalaUntilTermName,
      targs = Nil,
      args = List(Lit.Int(10), Lit.Int(11))
    )

    intercept[IllegalStateException] {
      TermApplyInfixToRangeTransformer.transform(termApplyInfix)
    }
  }

  test("transform '+' should throw an IllegalStateException") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(0),
      op = PlusTermName,
      targs = Nil,
      args = List(Lit.Int(10))
    )

    intercept[IllegalStateException] {
      TermApplyInfixToRangeTransformer.transform(termApplyInfix)
    }
  }
}
