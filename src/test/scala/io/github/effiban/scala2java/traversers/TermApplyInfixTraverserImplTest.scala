package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.classifiers.TermApplyInfixClassifier
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TermNames
import io.github.effiban.scala2java.testtrees.TermNames.{Plus, ScalaAssociation, ScalaRange, ScalaTo}
import io.github.effiban.scala2java.transformers.{TermApplyInfixToMapEntryTransformer, TermApplyInfixToRangeTransformer}

import scala.meta.Term

class TermApplyInfixTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val invocationArgListTraverser = mock[InvocationArgListTraverser]
  private val termApplyInfixClassifier = mock[TermApplyInfixClassifier]
  private val termApplyInfixToRangeTransformer = mock[TermApplyInfixToRangeTransformer]
  private val termApplyInfixToMapEntryTransformer = mock[TermApplyInfixToMapEntryTransformer]

  private val termApplyInfixTraverser = new TermApplyInfixTraverserImpl(
    termTraverser,
    termApplyTraverser,
    termNameTraverser,
    invocationArgListTraverser,
    termApplyInfixClassifier,
    termApplyInfixToRangeTransformer,
    termApplyInfixToMapEntryTransformer)

  test("traverse() when has range operator") {
    val lhs = Term.Name("a")
    val rhs = Term.Name("b")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = ScalaTo,
      targs = Nil,
      args = List(rhs)
    )

    val expectedRangeTermApply = Term.Apply(fun = ScalaRange, args = List(lhs, rhs))

    when(termApplyInfixClassifier.isRange(eqTree(applyInfix))).thenReturn(true)
    when(termApplyInfixToRangeTransformer.transform(eqTree(applyInfix))).thenReturn(expectedRangeTermApply)

    termApplyInfixTraverser.traverse(applyInfix)

    verify(termApplyTraverser).traverse(eqTree(expectedRangeTermApply))
  }

  test("traverse() when has association operator") {
    val lhs = Term.Name("a")
    val rhs = Term.Name("b")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = ScalaAssociation,
      targs = Nil,
      args = List(rhs)
    )

    val expectedMapEntryTermApply = Term.Apply(Term.Select(TermNames.Map, TermNames.JavaEntryMethod), args = List(lhs, rhs))

    when(termApplyInfixClassifier.isAssociation(eqTree(applyInfix))).thenReturn(true)

    when(termApplyInfixToMapEntryTransformer.transform(eqTree(applyInfix))).thenReturn(expectedMapEntryTermApply)

    termApplyInfixTraverser.traverse(applyInfix)

    verify(termApplyTraverser).traverse(eqTree(expectedMapEntryTermApply))
  }

  test("traverse() when has arithmetic operator") {
    val lhs = Term.Name("a")
    val op = Plus
    val rhs = Term.Name("b")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = op,
      targs = Nil,
      args = List(rhs)
    )

    doWrite("a").when(termTraverser).traverse(eqTree(lhs))
    doWrite("+").when(termNameTraverser).traverse(eqTree(op))
    doWrite("b").when(termTraverser).traverse(eqTree(rhs))

    termApplyInfixTraverser.traverse(applyInfix)

    outputWriter.toString shouldBe "a + b"
  }

}
