package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term
import scala.meta.Term.Return

class ReturnRendererImplTest extends UnitTestSuite {

  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val returnRenderer = new ReturnRendererImpl(expressionTermRenderer)

  test("render()") {
    val x = Term.Name("x")

    doWrite("x").when(expressionTermRenderer).render(eqTree(x))

    returnRenderer.render(Return(x))

    outputWriter.toString shouldBe "return x"
  }
}
