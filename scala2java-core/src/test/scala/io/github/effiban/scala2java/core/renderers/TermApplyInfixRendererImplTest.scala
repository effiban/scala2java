package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.Plus
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class TermApplyInfixRendererImplTest extends UnitTestSuite {
  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val termNameRenderer = mock[TermNameRenderer]

  private val termApplyInfixRenderer = new TermApplyInfixRendererImpl(
    expressionTermRenderer,
    termNameRenderer
  )


  test("render()") {
    val lhs = Term.Name("a")
    val op = Plus
    val rhs = Term.Name("b")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = op,
      targs = Nil,
      args = List(rhs)
    )

    doWrite("a").when(expressionTermRenderer).render(eqTree(lhs))
    doWrite("+").when(termNameRenderer).render(eqTree(op))
    doWrite("b").when(expressionTermRenderer).render(eqTree(rhs))

    termApplyInfixRenderer.render(applyInfix)

    outputWriter.toString shouldBe "a + b"
  }

}
