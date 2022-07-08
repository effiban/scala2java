package effiban.scala2java.transformers


import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term.{Apply, Select}
import scala.meta.{Lit, Term}

class TermInterpolateTransformerTest extends UnitTestSuite {

  test("transform 's' should return a Java String.format invocation") {
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

    val actualMaybeJavaStringFormat = TermInterpolateTransformer.transform(termInterpolate)

    actualMaybeJavaStringFormat.value.structure shouldBe expectedJavaStringFormat.structure
  }

  test("transform 'f' should return None (unsupported)") {
    val interpolationArgs = List(Term.Name("x"), Term.Name("y"))

    // f"start %s middle %d end"
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("f"),
      parts = List(Lit.String("start "), Lit.String(" middle "), Lit.String(" end")),
      args = interpolationArgs
    )

    val actualMaybeJavaStringFormat = TermInterpolateTransformer.transform(termInterpolate)

    actualMaybeJavaStringFormat shouldBe None
  }
}
