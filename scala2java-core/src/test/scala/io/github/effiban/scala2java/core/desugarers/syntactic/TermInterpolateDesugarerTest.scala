package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term.{Apply, Select}
import scala.meta.{Lit, Term}

class TermInterpolateDesugarerTest extends UnitTestSuite {

  test("desugar 's' should return a Java String.format invocation with added '%s' placeholders") {
    val interpolationArgs = List(Term.Name("x"), Term.Name("y"))

    // s"start $x middle $y end"
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("s"),
      parts = List(Lit.String("start "), Lit.String(" middle "), Lit.String(" end")),
      args = interpolationArgs
    )

    // String.format("start %s middle %s end", x, y)
    val expectedJavaStringFormat = Apply(
      fun = Select(Term.Name("String"), Term.Name("format")),
      args = Lit.String("start %s middle %s end") +: termInterpolate.args
    )

    val actualMaybeJavaStringFormat = TermInterpolateDesugarer.desugar(termInterpolate)

    actualMaybeJavaStringFormat.structure shouldBe expectedJavaStringFormat.structure
  }

  test("desugar 'f' should return a Java String.format invocation with no added placeholders") {
    val interpolationArgs = List(Term.Name("x"), Term.Name("y"))

    // f"start $x%d middle $y%f end"
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("f"),
      parts = List(Lit.String("start "), Lit.String("%d middle "), Lit.String("%f end")),
      args = interpolationArgs
    )

    // String.format("start %d middle %f end", x, y)
    val expectedJavaStringFormat = Apply(
      fun = Select(Term.Name("String"), Term.Name("format")),
      args = Lit.String("start %d middle %f end") +: termInterpolate.args
    )

    val actualMaybeJavaStringFormat = TermInterpolateDesugarer.desugar(termInterpolate)

    actualMaybeJavaStringFormat.structure shouldBe expectedJavaStringFormat.structure
  }

  test("desugar 'raw' should return a Java String.format invocation with no added placeholders") {
    val interpolationArgs = List(Term.Name("x"), Term.Name("y"))

    // raw"start $x%d middle $y%f end"
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("raw"),
      parts = List(Lit.String("start "), Lit.String("%d middle "), Lit.String("%f end")),
      args = interpolationArgs
    )

    // String.format("start %d middle %f end", x, y)
    val expectedJavaStringFormat = Apply(
      fun = Select(Term.Name("String"), Term.Name("format")),
      args = Lit.String("start %d middle %f end") +: termInterpolate.args
    )

    val actualMaybeJavaStringFormat = TermInterpolateDesugarer.desugar(termInterpolate)

    actualMaybeJavaStringFormat.structure shouldBe expectedJavaStringFormat.structure
  }

  test("desugar 'custom' should return unchanged") {
    val interpolationArgs = List(Term.Name("x"), Term.Name("y"))

    // custom"start %s middle %d end"
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("custom"),
      parts = List(Lit.String("start "), Lit.String(" middle "), Lit.String(" end")),
      args = interpolationArgs
    )

    TermInterpolateDesugarer.desugar(termInterpolate).structure shouldBe termInterpolate.structure
  }
}
