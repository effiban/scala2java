package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, Pat, XtensionQuasiquoteTerm}

class CaseRendererImplTest extends UnitTestSuite {

  private val StringPat = q"""value1"""
  private val Cond = q"x > 2"
  private val Body = q"3"

  private val patRenderer = mock[PatRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val caseRenderer = new CaseRendererImpl(
    patRenderer,
    expressionTermRenderer
  )


  test("render() non-default without condition") {
    doWrite(""""value1"""").when(patRenderer).render(eqTree(StringPat))
    doWrite("3").when(expressionTermRenderer).render(eqTree(Body))

    caseRenderer.render(
      Case(pat = StringPat,
        cond = None,
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """case "value1" -> 3;
        |""".stripMargin
  }

  test("render() default case without condition") {
    doWrite("3").when(expressionTermRenderer).render(eqTree(Body))

    caseRenderer.render(
      Case(pat = Pat.Wildcard(),
        cond = None,
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """default -> 3;
        |""".stripMargin
  }

  test("render() with condition") {
    doWrite(""""value1"""").when(patRenderer).render(eqTree(StringPat))
    doWrite("x > 2").when(expressionTermRenderer).render(eqTree(Cond))
    doWrite("3").when(expressionTermRenderer).render(eqTree(Body))

    caseRenderer.render(
      Case(pat = StringPat,
        cond = Some(Cond),
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """case "value1" && x > 2 -> 3;
        |""".stripMargin
  }
}
