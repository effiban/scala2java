package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class DeprecatedTermRepeatedTraverserImplTest extends UnitTestSuite {
  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]

  private val termRepeatedTraverser = new DeprecatedTermRepeatedTraverserImpl(expressionTermTraverser)

  test("traverse") {
    val expr = Term.Name("x")
    val termRepeated = Term.Repeated(expr)

    termRepeatedTraverser.traverse(termRepeated)

    verify(expressionTermTraverser).traverse(eqTree(expr))
  }
}
