package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeBounds
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeBoundsTraverserImplTest extends UnitTestSuite {
  private val TypeT = t"T"
  private val TypeU = t"U"

  private val TypeV = t"V"
  private val TypeW = t"W"

  private val typeTraverser = mock[TypeTraverser]

  val typeBoundsTraverser = new TypeBoundsTraverserImpl(typeTraverser)

  test("traverse() when has higher bound only") {
    val typeBounds = Type.Bounds(lo = None, hi = Some(TypeT))
    val traversedTypeBounds = Type.Bounds(lo = None, hi = Some(TypeV))

    doReturn(TypeV).when(typeTraverser).traverse(eqTree(TypeT))

    typeBoundsTraverser.traverse(typeBounds).structure shouldBe traversedTypeBounds.structure
  }

  test("traverse() when has lower non-Null bound only") {
    val typeBounds = Type.Bounds(lo = Some(TypeT), hi = None)
    val traversedTypeBounds = Type.Bounds(lo = Some(TypeV), hi = None)

    doReturn(TypeV).when(typeTraverser).traverse(eqTree(TypeT))

    typeBoundsTraverser.traverse(typeBounds).structure shouldBe traversedTypeBounds.structure
  }

  test("traverse() when has lower Null bound only") {
    val typeBounds = Type.Bounds(lo = Some(Type.Name("Null")), hi = None)

    typeBoundsTraverser.traverse(typeBounds).structure shouldBe TypeBounds.Empty.structure
  }

  test("traverse() when has no bounds") {
    val typeBounds = Type.Bounds(lo = None, hi = None)

    typeBoundsTraverser.traverse(typeBounds).structure shouldBe TypeBounds.Empty.structure
  }

  test("traverse() when has both bounds") {
    val typeBounds = Type.Bounds(lo = Some(TypeT), hi = Some(TypeU))
    val traversedTypeBounds = Type.Bounds(lo = Some(TypeV), hi = Some(TypeW))

    doReturn(TypeV).when(typeTraverser).traverse(eqTree(TypeT))
    doReturn(TypeW).when(typeTraverser).traverse(eqTree(TypeU))

    typeBoundsTraverser.traverse(typeBounds).structure shouldBe traversedTypeBounds.structure
  }
}
