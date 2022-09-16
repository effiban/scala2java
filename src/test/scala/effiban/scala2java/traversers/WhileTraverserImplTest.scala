package effiban.scala2java.traversers

import effiban.scala2java.contexts.BlockContext
import effiban.scala2java.matchers.BlockContextMatcher.eqBlockContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

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
