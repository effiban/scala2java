package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Term, XtensionQuasiquoteTerm}

class AssignRendererImplTest extends UnitTestSuite {

  private val assignLHSRenderer = mock[AssignLHSRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val assignRenderer = new AssignRendererImpl(
    assignLHSRenderer,
    expressionTermRenderer
  )

  test("render") {
    val lhs = q"x"
    val rhs = q"1"
    val assign = Term.Assign(lhs, rhs)

    doWrite("x = ").when(assignLHSRenderer).render(eqTree(lhs), asComment = eqTo(false))
    doWrite("1").when(expressionTermRenderer).render(eqTree(rhs))

    assignRenderer.render(assign)

    outputWriter.toString shouldBe "x = 1"
  }
}
