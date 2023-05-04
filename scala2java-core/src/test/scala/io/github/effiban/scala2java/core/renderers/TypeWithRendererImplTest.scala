package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeWithRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]

  private val typeWithRenderer = new TypeWithRendererImpl(typeRenderer)

  test("render") {
    val lhs = t"A"
    val rhs = t"B"
    val typeWith = t"A with B"

    doWrite("A").when(typeRenderer).render(eqTree(lhs))
    doWrite("B").when(typeRenderer).render(eqTree(rhs))

    typeWithRenderer.render(typeWith)

    outputWriter.toString shouldBe "A extends B"
  }
}
