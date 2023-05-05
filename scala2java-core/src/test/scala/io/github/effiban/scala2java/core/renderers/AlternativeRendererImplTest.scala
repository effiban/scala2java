package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Pat}

class AlternativeRendererImplTest extends UnitTestSuite {

  private val patRenderer = mock[PatRenderer]

  private val alternativeRenderer = new AlternativeRendererImpl(patRenderer)

  test("traverse") {
    val lhs = Lit.Int(3)
    val rhs = Lit.Int(4)

    doWrite("3").when(patRenderer).render(eqTree(lhs))
    doWrite("4").when(patRenderer).render(eqTree(rhs))

    alternativeRenderer.render(Pat.Alternative(lhs, rhs))

    outputWriter.toString shouldBe "3, 4"
  }
}
