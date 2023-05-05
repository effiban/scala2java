package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeWildcardTraverserImplTest extends UnitTestSuite {

  private val typeBoundsTraverser = mock[TypeBoundsTraverser]

  private val typeAnonymousParamTraverser = new TypeWildcardTraverserImpl(typeBoundsTraverser)

  test("traverse") {
    val bounds = Type.Bounds(lo = None, hi = Some(t"T"))
    val traversedBounds = Type.Bounds(lo = None, hi = Some(t"U"))

    doReturn(traversedBounds).when(typeBoundsTraverser).traverse(eqTree(bounds))

    typeAnonymousParamTraverser.traverse(Type.Wildcard(bounds)).structure shouldBe Type.Wildcard(traversedBounds).structure
  }
}
