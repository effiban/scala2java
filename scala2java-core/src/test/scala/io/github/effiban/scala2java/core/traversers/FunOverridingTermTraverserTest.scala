package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class FunOverridingTermTraverserTest extends UnitTestSuite {

  private val termRefTraverser = mock[ExpressionTermRefTraverser]
  private val mainApplyTypeTraverser = mock[MainApplyTypeTraverser]
  private val termTraverser = mock[TermTraverser]

  private val funOverridingTermTraverser = new FunOverridingTermTraverser(
    termRefTraverser,
    mainApplyTypeTraverser,
    termTraverser)

  test("traverse() when fun is a Term.Name should call the overriding TermRefTraverser") {
    val fun = q"abc"

    funOverridingTermTraverser.traverse(fun)

    verify(termRefTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.Select should call the overriding TermRefTraverser") {
    val fun = q"A.a"

    funOverridingTermTraverser.traverse(fun)

    verify(termRefTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.ApplyType should call the overriding MainApplyTypeTraverser") {
    val fun = q"a[Int]"

    funOverridingTermTraverser.traverse(fun)

    verify(mainApplyTypeTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.Apply should call the TermTraverser") {
    val fun = q"a(1)"

    funOverridingTermTraverser.traverse(fun)

    verify(termTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.ApplyInfix should call the TermTraverser") {
    val fun = q"a + b"

    funOverridingTermTraverser.traverse(fun)

    verify(termTraverser).traverse(eqTree(fun))
  }
}
