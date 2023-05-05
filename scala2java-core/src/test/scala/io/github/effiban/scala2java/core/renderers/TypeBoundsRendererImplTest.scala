package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type

class TypeBoundsRendererImplTest extends UnitTestSuite {
  private val TypeT = Type.Name("T")
  private val TypeU = Type.Name("U")

  private val typeRenderer = mock[TypeRenderer]

  val typeBoundsRenderer = new TypeBoundsRendererImpl(typeRenderer)

  test("traverse() when has higher bound only") {
    val typeBounds = Type.Bounds(lo = None, hi = Some(TypeT))

    doWrite("T").when(typeRenderer).render(eqTree(TypeT))

    typeBoundsRenderer.render(typeBounds)

    outputWriter.toString shouldBe " extends T"
  }

  test("traverse() when has lower bound only") {
    val typeBounds = Type.Bounds(lo = Some(TypeT), hi = None)

    doWrite("T").when(typeRenderer).render(eqTree(TypeT))

    typeBoundsRenderer.render(typeBounds)

    outputWriter.toString shouldBe " super T"
  }

  test("traverse() when has no bounds") {
    val typeBounds = Type.Bounds(lo = None, hi = None)

    typeBoundsRenderer.render(typeBounds)

    outputWriter.toString shouldBe ""
  }

  test("traverse() when has both bounds") {
    val typeBounds = Type.Bounds(lo = Some(TypeT), hi = Some(TypeU))

    doWrite("T").when(typeRenderer).render(eqTree(TypeT))
    doWrite("U").when(typeRenderer).render(eqTree(TypeU))

    typeBoundsRenderer.render(typeBounds)

    outputWriter.toString shouldBe "/*  >: T <: U */"
  }
}
