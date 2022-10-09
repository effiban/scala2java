package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.transformers.TermInterpolateTransformer

import scala.meta.Term.{Apply, Select}
import scala.meta.{Lit, Term}

class TermInterpolateTraverserImplTest extends UnitTestSuite {

  private val termInterpolateTransformer = mock[TermInterpolateTransformer]
  private val termApplyTraverser = mock[TermApplyTraverser]

  private val termInterpolateTraverser = new TermInterpolateTraverserImpl(termInterpolateTransformer, termApplyTraverser)

  test("traverse when supported") {
    val myVal = Term.Name("myVal")
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("s"),
      parts = List(Lit.String("start-"), Lit.String("-end")),
      args = List(myVal)
    )
    val interpolatedApply =
      Apply(
        fun = Select(Term.Name("String"), Term.Name("format")),
        args = List(Lit.String("start-%-end"), myVal)
      )
    val interpolatedString = """String.format("start-%s-end", myVal)"""

    when(termInterpolateTransformer.transform(eqTree(termInterpolate))).thenReturn(Some(interpolatedApply))
    doWrite(interpolatedString).when(termApplyTraverser).traverse(eqTree(interpolatedApply))

    termInterpolateTraverser.traverse(termInterpolate)

    outputWriter.toString shouldBe interpolatedString
  }

  test("traverse when unsupported") {
    val myVal = Term.Name("myVal")
    val termInterpolate = Term.Interpolate(
      prefix = Term.Name("f"),
      parts = List(Lit.String("start-"), Lit.String("%d-end")),
      args = List(myVal)
    )

    when(termInterpolateTransformer.transform(eqTree(termInterpolate))).thenReturn(None)

    termInterpolateTraverser.traverse(termInterpolate)

    outputWriter.toString shouldBe """/* UNSUPPORTED interpolation: f"start-$myVal%d-end" */"""
  }
}
