package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class DeprecatedClassOfTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val classOfTraverser = new DeprecatedClassOfTraverserImpl(typeTraverser)

  test("traverse() when there is one type") {
    val typeName = t"T"
    val classOfType = q"classOf[T]"
    val traversedTypeName = t"U"
    val traversedClassOfType = q"classOf[U]"

    doReturn(traversedTypeName).when(typeTraverser).traverse(eqTree(typeName))

    classOfTraverser.traverse(classOfType).structure shouldBe traversedClassOfType.structure
  }
}
