package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteType}

class PatTypedTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val patTraverser = mock[PatTraverser]

  val patTypedTraverser = new PatTypedTraverserImpl(patTraverser, typeTraverser)

  test("traverse()") {
    val lhs = p"x"
    val rhs = t"X"
    val patTyped = p"x: X"

    val traversedLhs = p"y"
    val traversedRhs = t"Y"
    val traversedPatTyped = p"y: Y"

    doReturn(traversedLhs).when(patTraverser).traverse(eqTree(lhs))
    doReturn(traversedRhs).when(typeTraverser).traverse(eqTree(rhs))

    patTypedTraverser.traverse(patTyped).structure shouldBe traversedPatTyped.structure
  }
}
