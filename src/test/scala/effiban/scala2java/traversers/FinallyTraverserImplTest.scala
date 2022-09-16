package effiban.scala2java.traversers

import effiban.scala2java.contexts.BlockContext
import effiban.scala2java.matchers.BlockContextMatcher.eqBlockContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

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
