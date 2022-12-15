package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Block, While}
import scala.meta.{Lit, Term}

class WhileTraverserImplTest extends UnitTestSuite {

  private val X = Term.Name("x")
  private val Expression = Term.ApplyInfix(lhs = X, op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3)))
  private val Statement = Term.Apply(fun = Term.Name("doSomething"), args = List(X))

  private val termTraverser = mock[TermTraverser]
  private val blockTraverser = mock[BlockTraverser]

  private val whileTraverser = new WhileTraverserImpl(termTraverser, blockTraverser)


  test("traverse() when body is a single statement") {
    val `while` = While(
      expr = Expression,
      body = Statement
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Expression))
    doWrite(
      """ {
        |  doSomething(x);
        |}
        |""".stripMargin).
      when(blockTraverser).traverse(stat = eqTree(Statement), context = eqBlockContext(BlockContext()))

    whileTraverser.traverse(`while`)

    outputWriter.toString shouldBe
      """while (x < 3) {
        |  doSomething(x);
        |}
        |""".stripMargin
  }

  test("traverse() when body is a block") {
    val `while` = While(
      expr = Expression,
      body = Block(List(Statement))
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Expression))
    doWrite(
      """ {
        |  doSomething(x);
        |}
        |""".stripMargin).
      when(blockTraverser).traverse(stat = eqTree(Block(List(Statement))), context = eqBlockContext(BlockContext()))

    whileTraverser.traverse(`while`)

    outputWriter.toString shouldBe
      """while (x < 3) {
        |  doSomething(x);
        |}
        |""".stripMargin
  }
}
