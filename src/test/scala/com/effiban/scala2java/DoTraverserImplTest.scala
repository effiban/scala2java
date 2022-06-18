package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import org.mockito.ArgumentMatchers

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

  private val termTraverser = mock[TermTraverser]
  private val blockTraverser = mock[BlockTraverser]

  private val doTraverser = new DoTraverserImpl(termTraverser, blockTraverser)


  test("traverse() when body is single statement") {
    val `do` = Do(
      body = Statement,
      expr = Expression
    )

    doWrite(
      """ {
        |  /* BODY */
        |}""".stripMargin)
      .when(blockTraverser).traverse(block = eqTree(Block(List(Statement))),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None))
    doWrite("x < 3").when(termTraverser).traverse(eqTree(Expression))

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
      .when(blockTraverser).traverse(block = eqTree(body),
      shouldReturnValue = ArgumentMatchers.eq(false),
      maybeInit = ArgumentMatchers.eq(None))
    doWrite("x < 3").when(termTraverser).traverse(eqTree(Expression))

    doTraverser.traverse(`do`)

    outputWriter.toString shouldBe
      """do {
        |  /* BODY */
        |} while (x < 3)""".stripMargin
  }
}
