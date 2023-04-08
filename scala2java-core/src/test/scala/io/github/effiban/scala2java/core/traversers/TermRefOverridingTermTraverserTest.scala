package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TermRefOverridingTermTraverserTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val termRefTraverser = mock[TermRefTraverser]

  private val termRefOverridingTermTraverser = new TermRefOverridingTermTraverser(termRefTraverser, termTraverser)

  test("traverse() when fun is a Term.Name should call the overriding TermRefTraverser") {
    val fun = q"abc"

    termRefOverridingTermTraverser.traverse(fun)

    verify(termRefTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.Select should call the overriding TermRefTraverser") {
    val fun = q"A.a"

    termRefOverridingTermTraverser.traverse(fun)

    verify(termRefTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.Apply should call the TermTraverser") {
    val fun = q"a(1)"

    termRefOverridingTermTraverser.traverse(fun)

    verify(termTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.ApplyType should call the TermTraverser") {
    val fun = q"a[Int]"

    termRefOverridingTermTraverser.traverse(fun)

    verify(termTraverser).traverse(eqTree(fun))
  }

  test("traverse() when fun is a Term.ApplyInfix should call the TermTraverser") {
    val fun = q"a + b"

    termRefOverridingTermTraverser.traverse(fun)

    verify(termTraverser).traverse(eqTree(fun))
  }
}
