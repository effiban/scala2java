package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class AssignLHSRendererImplTest extends UnitTestSuite {

  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val assignLHSRenderer = new AssignLHSRendererImpl(expressionTermRenderer)

  test("traverse when LHS should be traversed normally") {
    val lhs = q"myVal"

    doWrite("myVal").when(expressionTermRenderer).render(eqTree(lhs))

    assignLHSRenderer.render(lhs)

    outputWriter.toString shouldBe "myVal = "
  }

  test("traverse when LHS should be written as a comment") {
    val lhs = q"myVal"

    assignLHSRenderer.render(lhs, asComment = true)

    outputWriter.toString shouldBe "/* myVal = */"
  }
}
