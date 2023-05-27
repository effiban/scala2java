package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Apply, Select}
import scala.meta.{Lit, Term, XtensionQuasiquoteSource}

class SourceDesugarerTest extends UnitTestSuite {

  private val termInterpolateDesugarer = mock[TermInterpolateDesugarer]

  private val sourceDesugarer = new SourceDesugarerImpl(termInterpolateDesugarer)

  test("desugar when has a Term.Interpolate should return a desugared equivalent") {
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

    val source =
      source"""
      package dummy

      class MyClass {
         val x = $termInterpolate
      }
      """

    doReturn(expectedJavaStringFormat).when(termInterpolateDesugarer).desugar(eqTree(termInterpolate))

    val desugaredSource = sourceDesugarer.desugar(source)

    val maybeDesugaredTermApply = desugaredSource.collect { case termApply: Term.Apply => termApply }.headOption

    maybeDesugaredTermApply.value.structure shouldBe expectedJavaStringFormat.structure
  }

  test("desugar with no inner desugared elems should return unchanged") {
    val source =
      source"""
      package dummy

      class MyClass {
        val x = 3
      }
      """

    sourceDesugarer.desugar(source).structure shouldBe source.structure
  }
}
