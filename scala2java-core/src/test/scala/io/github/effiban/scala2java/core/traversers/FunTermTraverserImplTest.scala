package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class FunTermTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val funTermTraverser = new FunTermTraverserImpl(expressionTermTraverser)

  test("traverse() should call expression traverser") {
    val fun = q"abc"

    funTermTraverser.traverse(fun)

    verify(expressionTermTraverser).traverse(eqTree(fun))
  }

}
