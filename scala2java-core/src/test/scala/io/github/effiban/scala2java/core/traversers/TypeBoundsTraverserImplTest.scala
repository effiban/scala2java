package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Type

class TypeBoundsTraverserImplTest extends UnitTestSuite {
  private val TypeT = Type.Name("T")
  private val TypeU = Type.Name("U")

  private val typeTraverser = mock[TypeTraverser]

  val typeBoundsTraverser = new TypeBoundsTraverserImpl(typeTraverser)

  test("traverse() when has higher bound only") {
    val typeBounds = Type.Bounds(lo = None, hi = Some(TypeT))

    doWrite("T").when(typeTraverser).traverse(eqTree(TypeT))

    typeBoundsTraverser.traverse(typeBounds)

    outputWriter.toString shouldBe " extends T"
  }

  test("traverse() when has lower non-Null bound only") {
    val typeBounds = Type.Bounds(lo = Some(TypeT), hi = None)

    doWrite("T").when(typeTraverser).traverse(eqTree(TypeT))

    typeBoundsTraverser.traverse(typeBounds)

    outputWriter.toString shouldBe " super T"
  }

  test("traverse() when has lower Null bound only") {
    val typeBounds = Type.Bounds(lo = Some(Type.Name("Null")), hi = None)

    typeBoundsTraverser.traverse(typeBounds)

    outputWriter.toString shouldBe ""
  }

  test("traverse() when has no bounds") {
    val typeBounds = Type.Bounds(lo = None, hi = None)

    typeBoundsTraverser.traverse(typeBounds)

    outputWriter.toString shouldBe ""
  }

  test("traverse() when has both bounds") {
    val typeBounds = Type.Bounds(lo = Some(TypeT), hi = Some(TypeU))

    doWrite("T").when(typeTraverser).traverse(eqTree(TypeT))
    doWrite("U").when(typeTraverser).traverse(eqTree(TypeU))

    typeBoundsTraverser.traverse(typeBounds)

    outputWriter.toString shouldBe "/*  >: T <: U */"
  }
}
