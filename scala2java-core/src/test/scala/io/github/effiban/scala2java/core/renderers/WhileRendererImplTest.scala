package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Block, While}
import scala.meta.{Lit, Term}

class WhileRendererImplTest extends UnitTestSuite {

  private val X = Term.Name("x")
  private val Expression = Term.ApplyInfix(lhs = X, op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3)))
  private val Statement = Term.Apply(fun = Term.Name("doSomething"), args = List(X))

  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val defaultTermRenderer = mock[DefaultTermRenderer]

  private val whileRenderer = new WhileRendererImpl(expressionTermRenderer, defaultTermRenderer)


  test("render()") {
    val `while` = While(
      expr = Expression,
      body = Block(List(Statement))
    )

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Expression))
    doWrite(
      """ {
        |  doSomething(x);
        |}
        |""".stripMargin).
      when(defaultTermRenderer).render(eqTree(Block(List(Statement))))

    whileRenderer.render(`while`)

    outputWriter.toString shouldBe
      """while (x < 3) {
        |  doSomething(x);
        |}
        |""".stripMargin
  }
}
