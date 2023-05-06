package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteCaseOrPattern

class AlternativeTraverserImplTest extends UnitTestSuite {

  private val patTraverser = mock[PatTraverser]

  private val alternativeTraverser = new AlternativeTraverserImpl(patTraverser)

  test("traverse") {
    val lhs = p"x"
    val rhs = p"y"
    val alternative = p"x | y"

    val traversedLhs = p"xx"
    val traversedRhs = p"yy"
    val traversedAlternative = p"xx | yy"

    doReturn(traversedLhs).when(patTraverser).traverse(eqTree(lhs))
    doReturn(traversedRhs).when(patTraverser).traverse(eqTree(rhs))

    alternativeTraverser.traverse(alternative).structure shouldBe traversedAlternative.structure
  }
}
