package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeExistentialTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeExistentialTraverser = new TypeExistentialTraverserImpl(typeTraverser)

  test("traverse") {
    val tpe = t"T[A]"
    val traversedType = t"U[A]"

    val typeExistential = t"T[A] forSome { type A }"
    val traversedTypeExistential = t"U[A] forSome { type A }"

    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))

    typeExistentialTraverser.traverse(typeExistential).structure shouldBe traversedTypeExistential.structure
  }
}
