package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class StatTermTraverserImplTest extends UnitTestSuite {

  private val expressionTermRefTraverser = mock[ExpressionTermRefTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]

  private val statTermTraverser = new StatTermTraverserImpl(
    expressionTermRefTraverser,
    defaultTermTraverser)

  test("traverse() when fun is a Term.Name should call the ExpressionTermRefTraverser") {
    val fun = q"abc"

    statTermTraverser.traverse(fun)

    verify(expressionTermRefTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.Select should call the ExpressionTermRefTraverser") {
    val fun = q"A.a"

    statTermTraverser.traverse(fun)

    verify(expressionTermRefTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.ApplyType should call the DefaultTermTraverser") {
    val fun = q"a[Int]"

    statTermTraverser.traverse(fun)

    verify(defaultTermTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.Apply should call the DefaultTermTraverser") {
    val fun = q"a(1)"

    statTermTraverser.traverse(fun)

    verify(defaultTermTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.ApplyInfix should call the DefaultTermTraverser") {
    val fun = q"a + b"

    statTermTraverser.traverse(fun)

    verify(defaultTermTraverser).traverse(eqTree(fun))
  }
}
