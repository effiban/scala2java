package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class ApplyUnaryRendererImplTest extends UnitTestSuite {

  private val termNameRenderer = mock[TermNameRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val applyUnaryRenderer = new ApplyUnaryRendererImpl(termNameRenderer, expressionTermRenderer)

  test("render") {
    val op = Term.Name("!")
    val arg = Term.Name("myFlag")

    doWrite("!").when(termNameRenderer).render(eqTree(op))
    doWrite("myFlag").when(expressionTermRenderer).render(eqTree(arg))

    applyUnaryRenderer.render(Term.ApplyUnary(op = op, arg = arg))

    outputWriter.toString shouldBe "!myFlag"
  }
}
