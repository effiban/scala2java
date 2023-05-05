package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeRepeatedTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  val typeRepeatedTraverser = new TypeRepeatedTraverserImpl(typeTraverser)

  test("traverse()") {
    val tpe = t"T"
    val traversedType = t"U"

    val repeatedType = t"T*"
    val traversedRepeatedType = t"U*"

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))

    typeRepeatedTraverser.traverse(repeatedType).structure shouldBe traversedRepeatedType.structure
  }
}
