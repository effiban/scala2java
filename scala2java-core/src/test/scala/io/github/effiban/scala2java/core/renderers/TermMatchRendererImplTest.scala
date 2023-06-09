package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, Lit, Term}

class TermMatchRendererImplTest extends UnitTestSuite {

  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val caseRenderer = mock[CaseRenderer]

  private val termMatchRenderer = new TermMatchRendererImpl(expressionTermRenderer, caseRenderer)

  test("render") {
    val expr = Term.Name("x")
    val case1 = Case(pat = Lit.Int(1), cond = None, body = Lit.String("one"))
    val case2 = Case(pat = Lit.Int(2), cond = None, body = Lit.String("two"))

    val termMatch = Term.Match(
      expr = expr,
      cases = List(case1, case2),
      mods = Nil
    )
    doWrite("x").when(expressionTermRenderer).render(eqTree(expr))
    doWrite(
      """  case 1 -> "one";
        |""".stripMargin).when(caseRenderer).render(eqTree(case1))
    doWrite(
      """  case 2 -> "two";
        |""".stripMargin).when(caseRenderer).render(eqTree(case2))

    termMatchRenderer.render(termMatch)

    outputWriter.toString shouldBe
    """switch (x) {
        |  case 1 -> "one";
        |  case 2 -> "two";
        |}
        |""".stripMargin
  }
}
