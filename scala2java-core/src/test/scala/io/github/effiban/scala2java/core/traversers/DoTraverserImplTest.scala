package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{ApplyInfix, Block, Do}
import scala.meta.{Lit, Term}

class DoTraverserImplTest extends UnitTestSuite {

  private val X = Term.Name("x")

  private val Statement = Term.Apply(
    fun = Term.Name("someOperation"),
    args = List(X))

  private val Expression = ApplyInfix(
    lhs = X,
    op = Term.Name("<"),
    targs = Nil,
    args = List(Lit.Int(3)))

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val blockTraverser = mock[BlockTraverser]

  private val doTraverser = new DoTraverserImpl(expressionTermTraverser, blockTraverser)


  test("traverse() when body is single statement") {
    val `do` = Do(
      body = Statement,
      expr = Expression
    )

    doWrite(
      """ {
        |  /* BODY */
        |}""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement), context = eqBlockContext(BlockContext())
    )
    doWrite("x < 3").when(expressionTermTraverser).traverse(eqTree(Expression))

    doTraverser.traverse(`do`)

    outputWriter.toString shouldBe
      """do {
        |  /* BODY */
        |} while (x < 3)""".stripMargin
  }

  test("traverse() when body is a block") {
    val body = Block(
      List(
        Term.Apply(
          fun = Term.Name("someOperation"),
          args = List(X))
      )
    )

    val `do` = Do(
      body = body,
      expr = ApplyInfix(
        lhs = X,
        op = Term.Name("<"),
        targs = Nil,
        args = List(Lit.Int(3)))
    )

    doWrite(
      """ {
        |  /* BODY */
        |}""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(body), context = eqBlockContext(BlockContext())
    )
    doWrite("x < 3").when(expressionTermTraverser).traverse(eqTree(Expression))

    doTraverser.traverse(`do`)

    outputWriter.toString shouldBe
      """do {
        |  /* BODY */
        |} while (x < 3)""".stripMargin
  }
}
