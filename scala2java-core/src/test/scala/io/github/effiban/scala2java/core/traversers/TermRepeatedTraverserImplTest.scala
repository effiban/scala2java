package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class TermRepeatedTraverserImplTest extends UnitTestSuite {
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val termRepeatedTraverser = new TermRepeatedTraverserImpl(expressionTermTraverser)

  test("traverse") {
    val expr = Term.Name("x")
    val termRepeated = Term.Repeated(expr)

    termRepeatedTraverser.traverse(termRepeated)

    verify(expressionTermTraverser).traverse(eqTree(expr))
  }
}
