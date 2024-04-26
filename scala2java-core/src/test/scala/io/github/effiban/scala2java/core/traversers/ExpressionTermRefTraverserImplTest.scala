package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermRefTraverserImplTest extends UnitTestSuite {

  private val expressionTermSelectTraverser = mock[ExpressionTermSelectTraverser]
  private val applyUnaryTraverser = mock[ApplyUnaryTraverser]
  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]

  private val expressionTermRefTraverser = new ExpressionTermRefTraverserImpl(
    expressionTermSelectTraverser,
    applyUnaryTraverser,
    defaultTermRefTraverser
  )

  test("traverse() for Term.Select") {
    val termSelect = q"x.y"
    val traversedTerm = q"w.z(2, 3)"

    doReturn(traversedTerm).when(expressionTermSelectTraverser).traverse(eqTree(termSelect))

    expressionTermRefTraverser.traverse(termSelect).structure shouldBe traversedTerm.structure
  }

  test("traverse() for Term.ApplyUnary") {
    val applyUnary = q"!flag"
    val traversedApplyUnary = q"!traversedFlag"

    doReturn(traversedApplyUnary).when(applyUnaryTraverser).traverse(eqTree(applyUnary))

    expressionTermRefTraverser.traverse(applyUnary).structure shouldBe traversedApplyUnary.structure
  }

  test("traverse() for Term.This") {
    val termThis = q"MyScalaClass.this"
    val traversedTermThis = q"MyJavaClass.this"

    doReturn(traversedTermThis).when(defaultTermRefTraverser).traverse(eqTree(termThis))

    expressionTermRefTraverser.traverse(termThis).structure shouldBe traversedTermThis.structure
  }

  test("traverse() for Term.Name") {
    val termName = q"x"
    val traversedTermName = q"xx"

    doReturn(traversedTermName).when(defaultTermRefTraverser).traverse(eqTree(termName))

    expressionTermRefTraverser.traverse(termName).structure shouldBe traversedTermName.structure
  }
}
