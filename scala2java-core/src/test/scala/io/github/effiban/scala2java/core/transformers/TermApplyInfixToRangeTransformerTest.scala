package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermNames
import io.github.effiban.scala2java.core.entities.TermNames.{Plus, ScalaInclusive, ScalaUntil}
import io.github.effiban.scala2java.core.entities.TermSelects.ScalaRange
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.ScalaTo

import scala.meta.{Lit, Term}

class TermApplyInfixToRangeTransformerTest extends UnitTestSuite {

  test("transform 'to' with one arg should return a scala.Range method call") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(1),
      op = ScalaTo,
      targs = Nil,
      args = List(Lit.Int(10))
    )

    val expectedTermApply = Term.Apply(
      fun = Term.Select(ScalaRange, ScalaInclusive),
      args = List(Lit.Int(1), Lit.Int(10))
    )

    TermApplyInfixToRangeTransformer.transform(termApplyInfix).value.structure shouldBe expectedTermApply.structure
  }

  test("transform 'to' with no args should throw an IllegalStateException") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(1),
      op = ScalaTo,
      targs = Nil,
      args = Nil
    )

    intercept[IllegalStateException] {
      TermApplyInfixToRangeTransformer.transform(termApplyInfix)
    }
  }

  test("transform 'until' with one arg") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(0),
      op = ScalaUntil,
      targs = Nil,
      args = List(Lit.Int(10))
    )

    val expectedTermApply = Term.Apply(
      fun = Term.Select(ScalaRange, TermNames.Apply),
      args = List(Lit.Int(0), Lit.Int(10))
    )

    TermApplyInfixToRangeTransformer.transform(termApplyInfix).value.structure shouldBe expectedTermApply.structure
  }

  test("transform 'until' with two args should throw an IllegalStateException") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(0),
      op = ScalaUntil,
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
      op = Plus,
      targs = Nil,
      args = List(Lit.Int(10))
    )

    intercept[IllegalStateException] {
      TermApplyInfixToRangeTransformer.transform(termApplyInfix)
    }
  }
}
