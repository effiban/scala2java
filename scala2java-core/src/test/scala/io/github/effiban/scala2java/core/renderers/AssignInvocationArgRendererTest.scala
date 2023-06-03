package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Term, XtensionQuasiquoteTerm}

class AssignInvocationArgRendererTest extends UnitTestSuite {

  private val assignLHSRenderer = mock[AssignLHSRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val assignInvocationArgRenderer = new AssignInvocationArgRenderer(
    assignLHSRenderer,
    expressionTermRenderer
  )

  test("render when argNameAsComment = false") {
    val lhs = q"x"
    val rhs = q"1"
    val assign = Term.Assign(lhs, rhs)
    val context = ArgumentContext()

    doWrite("x = ").when(assignLHSRenderer).render(eqTree(lhs), asComment = eqTo(false))
    doWrite("1").when(expressionTermRenderer).render(eqTree(rhs))

    assignInvocationArgRenderer.render(assign, context)

    outputWriter.toString shouldBe "x = 1"
  }

  test("render when argNameAsComment = true") {
    val lhs = q"x"
    val rhs = q"1"
    val assign = Term.Assign(lhs, rhs)
    val context = ArgumentContext(argNameAsComment = true)

    doWrite("/* x = */").when(assignLHSRenderer).render(eqTree(lhs), asComment = eqTo(true))
    doWrite("1").when(expressionTermRenderer).render(eqTree(rhs))

    assignInvocationArgRenderer.render(assign, context)

    outputWriter.toString shouldBe "/* x = */1"
  }

}
