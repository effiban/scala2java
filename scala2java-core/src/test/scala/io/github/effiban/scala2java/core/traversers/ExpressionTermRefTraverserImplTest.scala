package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermRefTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]

  private val expressionTermRefTraverser = new ExpressionTermRefTraverserImpl(defaultTermRefTraverser)

  test("traverse() for Term.This") {
    val termThis = q"MyScalaClass.this"
    val traversedTermThis = q"MyJavaClass.this"

    doReturn(traversedTermThis).when(defaultTermRefTraverser).traverse(eqTree(termThis))

    expressionTermRefTraverser.traverse(termThis).structure shouldBe traversedTermThis.structure
  }
}
