package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.entities.TermNames
import io.github.effiban.scala2java.core.entities.TermNames.{Plus, ScalaInclusive, ScalaTo, ScalaUntil}
import io.github.effiban.scala2java.core.entities.TermSelects.ScalaRange
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Lit, Term}

class TermApplyInfixToRangeDesugarerTest extends UnitTestSuite {
  
  test("desugar 'to' with one arg should return a scala.collection.immutable.Range method call") {
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

    TermApplyInfixToRangeDesugarer.desugar(termApplyInfix).structure shouldBe expectedTermApply.structure
  }

  test("desugar 'to' with no args should throw an IllegalStateException") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(1),
      op = ScalaTo,
      targs = Nil,
      args = Nil
    )

    intercept[IllegalStateException] {
      TermApplyInfixToRangeDesugarer.desugar(termApplyInfix)
    }
  }

  test("desugar 'until' with one arg") {
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

    TermApplyInfixToRangeDesugarer.desugar(termApplyInfix).structure shouldBe expectedTermApply.structure
  }

  test("desugar 'until' with two args should throw an IllegalStateException") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(0),
      op = ScalaUntil,
      targs = Nil,
      args = List(Lit.Int(10), Lit.Int(11))
    )

    intercept[IllegalStateException] {
      TermApplyInfixToRangeDesugarer.desugar(termApplyInfix)
    }
  }

  test("desugar '+' should throw an IllegalStateException") {
    val termApplyInfix = Term.ApplyInfix(
      lhs = Lit.Int(0),
      op = Plus,
      targs = Nil,
      args = List(Lit.Int(10))
    )

    intercept[IllegalStateException] {
      TermApplyInfixToRangeDesugarer.desugar(termApplyInfix)
    }
  }

}
