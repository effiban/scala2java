package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{ApplyInfix, Block, Do}
import scala.meta.{Lit, Term}

class DoRendererImplTest extends UnitTestSuite {

  private val X = Term.Name("x")

  private val Expression = ApplyInfix(
    lhs = X,
    op = Term.Name("<"),
    targs = Nil,
    args = List(Lit.Int(3)))

  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val defaultTermRenderer = mock[DefaultTermRenderer]

  private val doRenderer = new DoRendererImpl(expressionTermRenderer, defaultTermRenderer)


  test("render()") {
    val body = Block(
      List(
        Term.Apply(
          fun = Term.Name("someOperation"),
          args = List(X))
      )
    )

    val `do` = Do(body = body, expr = Expression)

    doWrite(
      """ {
        |  /* BODY */
        |}""".stripMargin)
      .when(defaultTermRenderer).render(eqTree(body))
    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Expression))

    doRenderer.render(`do`)

    outputWriter.toString shouldBe
      """do {
        |  /* BODY */
        |} while (x < 3)""".stripMargin
  }
}
