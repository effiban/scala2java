package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Self, XtensionQuasiquoteType}

class SelfTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val selfTraverser = new SelfTraverserImpl(typeTraverser)

  test("traverse when has no type") {
    val `self` = Self(name = Name.Indeterminate("SelfName"), decltpe = None)
    selfTraverser.traverse(`self`).structure shouldBe `self`.structure
  }

  test("traverse when has a type") {
    val selfName = Name.Indeterminate("SelfName")
    val selfType = t"SelfType"
    val traversedSelfType = t"TraversedSelfType"
    val `self` = Self(name = selfName, decltpe = Some(selfType))
    val traversedSelf = Self(name = selfName, decltpe = Some(traversedSelfType))

    doReturn(traversedSelfType).when(typeTraverser).traverse(eqTree(selfType))

    selfTraverser.traverse(`self`).structure shouldBe traversedSelf.structure
  }
}
