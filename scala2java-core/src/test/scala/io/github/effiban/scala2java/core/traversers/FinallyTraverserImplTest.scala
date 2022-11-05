package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term
import scala.meta.Term.Block

class FinallyTraverserImplTest extends UnitTestSuite {

  private val Statement = Term.Apply(Term.Name("log"), List(Term.Name("error")))

  private val blockTraverser = mock[BlockTraverser]

  private val finallyTraverser = new FinallyTraverserImpl(blockTraverser)

  test("traverse() when body is a statement") {
    doWrite(
      """ {
        |  log(error);
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Statement), context = eqBlockContext(BlockContext()))

    finallyTraverser.traverse(Statement)

    outputWriter.toString shouldBe
      """finally {
        |  log(error);
        |}
        |""".stripMargin
  }

  test("traverse() when body is a block") {
    doWrite(
      """ {
        |  log(error);
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      stat = eqTree(Block(List(Statement))), context = eqBlockContext(BlockContext()))

    finallyTraverser.traverse(Block(List(Statement)))

    outputWriter.toString shouldBe
      """finally {
        |  log(error);
        |}
        |""".stripMargin
  }
}
