package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeWithTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeWithTraverser = new TypeWithTraverserImpl(typeTraverser)

  test("traverse") {
    val lhs = t"L1"
    val rhs = t"R1"
    val typeWith = t"L1 with R1"
    val traversedLhs = t"L2"
    val traversedRhs = t"R2"
    val traversedTypeWith = t"L2 with R2"

    doReturn(traversedLhs).when(typeTraverser).traverse(eqTree(lhs))
    doReturn(traversedRhs).when(typeTraverser).traverse(eqTree(rhs))

    typeWithTraverser.traverse(typeWith).structure shouldBe traversedTypeWith.structure
  }
}
