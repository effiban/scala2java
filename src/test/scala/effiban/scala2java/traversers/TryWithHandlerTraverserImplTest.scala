package effiban.scala2java.traversers

import effiban.scala2java.contexts.{BlockContext, TryContext}
import effiban.scala2java.entities.Decision.Yes
import effiban.scala2java.matchers.BlockContextMatcher.eqBlockContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term
import scala.meta.Term.Block

class TryWithHandlerTraverserImplTest extends UnitTestSuite {

  private val TryStatement = Term.Apply(Term.Name("doSomething"), Nil)

  private val CatchHandler = Term.Name("someCatchHandler")

  private val FinallyStatement = Term.Apply(Term.Name("cleanup"), Nil)


  private val blockTraverser = mock[BlockTraverser]
  private val finallyTraverser = mock[FinallyTraverser]

  private val tryWithHandlerTraverser = new TryWithHandlerTraverserImpl(blockTraverser, finallyTraverser)

  test("traverse with a single statement and no 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = CatchHandler,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(TryStatement), context = eqBlockContext(BlockContext()))

    tryWithHandlerTraverser.traverse(tryWithHandler)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |/* UNPARSEABLE catch handler: someCatchHandler */
        |""".stripMargin
  }

  test("traverse with a single statement and no 'finally', and shouldReturnValue=Yes") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = CatchHandler,
      finallyp = None
    )

    doWrite(
      """ {
        |  return doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(
      stat = eqTree(TryStatement),context = eqBlockContext(BlockContext(shouldReturnValue = Yes)))

    tryWithHandlerTraverser.traverse(tryWithHandler = tryWithHandler, context = TryContext(shouldReturnValue = Yes))

    outputWriter.toString shouldBe
      """try {
        |  return doSomething();
        |}
        |/* UNPARSEABLE catch handler: someCatchHandler */
        |""".stripMargin
  }

  test("traverse with single statement and a 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryStatement,
      catchp = CatchHandler,
      finallyp = Some(FinallyStatement)
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(TryStatement), context = eqBlockContext(BlockContext()))

    doWrite(
      """finally {
        |  cleanup();
        |}
        |""".stripMargin)
      .when(finallyTraverser).traverse(eqTree(FinallyStatement))

    tryWithHandlerTraverser.traverse(tryWithHandler)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |/* UNPARSEABLE catch handler: someCatchHandler */
        |finally {
        |  cleanup();
        |}
        |""".stripMargin
  }

  test("traverse with a block and no 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = Block(List(TryStatement)),
      catchp = CatchHandler,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(Block(List(TryStatement))), context = eqBlockContext(BlockContext()))

    tryWithHandlerTraverser.traverse(tryWithHandler)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |/* UNPARSEABLE catch handler: someCatchHandler */
        |""".stripMargin
  }
}
