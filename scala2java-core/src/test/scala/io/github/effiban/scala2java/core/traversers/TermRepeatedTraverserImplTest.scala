package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermRepeatedTraverserImplTest extends UnitTestSuite {
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val termRepeatedTraverser = new TermRepeatedTraverserImpl(expressionTermTraverser)

  test("traverse") {
    val expr = q"x"
    val traversedExpr = q"xx"

    val termRepeated = Term.Repeated(expr)
    val traversedTermRepeated = Term.Repeated(traversedExpr)

    doReturn(traversedExpr).when(expressionTermTraverser).traverse(eqTree(expr))

    termRepeatedTraverser.traverse(termRepeated).structure shouldBe traversedTermRepeated.structure
  }
}
