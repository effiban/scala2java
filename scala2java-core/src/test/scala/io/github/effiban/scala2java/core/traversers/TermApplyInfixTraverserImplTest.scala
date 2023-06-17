package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.{Plus, ScalaRange, ScalaTo}
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Term

class TermApplyInfixTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]
  private val termApplyInfixToTermApplyTransformer = mock[TermApplyInfixToTermApplyTransformer]

  private val termApplyInfixTraverser = new TermApplyInfixTraverserImpl(
    expressionTermTraverser,
    termApplyTraverser,
    termApplyInfixToTermApplyTransformer)


  test("traverse() when transformed to a Term.Apply should call the TermApplyTraverser") {
    val lhs = Term.Name("a")
    val rhs = Term.Name("b")

    val traversedLhs = Term.Name("aa")
    val traversedRhs = Term.Name("bb")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = ScalaTo,
      targs = Nil,
      args = List(rhs)
    )

    val expectedRangeTermApply = Term.Apply(fun = ScalaRange, args = List(lhs, rhs))
    val expectedTraversedTermApply = Term.Apply(fun = ScalaRange, args = List(traversedLhs, traversedRhs))

    when(termApplyInfixToTermApplyTransformer.transform(eqTree(applyInfix))).thenReturn(Some(expectedRangeTermApply))
    doReturn(expectedTraversedTermApply).when(termApplyTraverser).traverse(eqTree(expectedRangeTermApply))

    termApplyInfixTraverser.traverse(applyInfix).structure shouldBe expectedTraversedTermApply.structure
  }

  test("traverse() when not transformed to a Term.Apply should traverse as an infix") {
    val lhs = Term.Name("a")
    val op = Plus
    val rhs = Term.Name("b")

    val traversedLhs = Term.Name("aa")
    val traversedRhs = Term.Name("bb")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = op,
      targs = Nil,
      args = List(rhs)
    )

    val expectedTraversedApplyInfix = Term.ApplyInfix(
      lhs = traversedLhs,
      op = op,
      targs = Nil,
      args = List(traversedRhs)
    )

    when(termApplyInfixToTermApplyTransformer.transform(eqTree(applyInfix))).thenReturn(None)

    doAnswer((arg: Term) => arg match {
      case anArg if anArg.structure == lhs.structure => traversedLhs
      case anArg if anArg.structure == rhs.structure => traversedRhs
    }).when(expressionTermTraverser).traverse(any[Term])

    termApplyInfixTraverser.traverse(applyInfix).structure shouldBe expectedTraversedApplyInfix.structure
  }

}
