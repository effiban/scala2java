package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.{Plus, ScalaRange, ScalaTo}
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class DeprecatedTermApplyInfixTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]
  private val termApplyTraverser = mock[DeprecatedTermApplyTraverser]
  private val termNameRenderer = mock[TermNameRenderer]
  private val termApplyInfixToTermApplyTransformer = mock[TermApplyInfixToTermApplyTransformer]

  private val termApplyInfixTraverser = new DeprecatedTermApplyInfixTraverserImpl(
    expressionTermTraverser,
    termApplyTraverser,
    termNameRenderer,
    termApplyInfixToTermApplyTransformer)


  test("traverse() when transformed to a Term.Apply should call the TermApplyTraverser") {
    val lhs = Term.Name("a")
    val rhs = Term.Name("b")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = ScalaTo,
      targs = Nil,
      args = List(rhs)
    )

    val expectedRangeTermApply = Term.Apply(fun = ScalaRange, args = List(lhs, rhs))

    when(termApplyInfixToTermApplyTransformer.transform(eqTree(applyInfix))).thenReturn(Some(expectedRangeTermApply))

    termApplyInfixTraverser.traverse(applyInfix)

    verify(termApplyTraverser).traverse(eqTree(expectedRangeTermApply))
  }

  test("traverse() when not transformed to a Term.Apply should traverse as an infix") {
    val lhs = Term.Name("a")
    val op = Plus
    val rhs = Term.Name("b")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = op,
      targs = Nil,
      args = List(rhs)
    )

    when(termApplyInfixToTermApplyTransformer.transform(eqTree(applyInfix))).thenReturn(None)
    doWrite("a").when(expressionTermTraverser).traverse(eqTree(lhs))
    doWrite("+").when(termNameRenderer).render(eqTree(op))
    doWrite("b").when(expressionTermTraverser).traverse(eqTree(rhs))

    termApplyInfixTraverser.traverse(applyInfix)

    outputWriter.toString shouldBe "a + b"
  }

}