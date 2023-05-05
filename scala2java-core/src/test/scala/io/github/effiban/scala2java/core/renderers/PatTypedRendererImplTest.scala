package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pat, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteType}

class PatTypedRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]
  private val patRenderer = mock[PatRenderer]

  val patTypedRenderer = new PatTypedRendererImpl(
    typeRenderer,
    patRenderer
  )

  test("traverse()") {
    val lhs = p"x"
    val rhs = t"MyType"

    doWrite("MyType").when(typeRenderer).render(eqTree(rhs))
    doWrite("x").when(patRenderer).render(eqTree(lhs))

    patTypedRenderer.render(Pat.Typed(lhs = lhs, rhs = rhs))

    outputWriter.toString shouldBe "MyType x"
  }
}
