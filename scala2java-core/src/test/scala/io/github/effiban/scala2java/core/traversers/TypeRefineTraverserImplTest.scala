package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeRefineTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeRefineTraverser = new TypeRefineTraverserImpl(typeTraverser)

  test("traverse when has type + stats") {
    val tpe = t"A"
    val refinedType = t"A {def fun(param: Int): Unit}"

    val traversedType = t"B"
    val traversedRefinedType = t"B {def fun(param: Int): Unit}"

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))

    typeRefineTraverser.traverse(refinedType).structure shouldBe traversedRefinedType.structure
  }

  test("traverse when has stats only") {
    val refinedType = t"{def fun(param: Int): Unit}"

    typeRefineTraverser.traverse(refinedType).structure shouldBe refinedType.structure
  }
}
