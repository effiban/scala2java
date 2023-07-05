package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, SimpleBlockStatRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.matchers.BlockRenderContextMockitoMatcher.eqBlockRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TryWithHandlerRendererImplTest extends UnitTestSuite {

  private val TryBlock = Term.Block(List(Term.Apply(Term.Name("doSomething"), Nil)))

  private val CatchHandler = q"someCatchHandler"

  private val FinallyBlock = Term.Block(List(Term.Apply(Term.Name("cleanup"), Nil)))

  private val blockRenderer = mock[BlockRenderer]
  private val finallyRenderer = mock[FinallyRenderer]

  private val tryWithHandlerRenderer = new TryWithHandlerRendererImpl(
    blockRenderer,
    finallyRenderer
  )

  test("render without a 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryBlock,
      catchp = CatchHandler,
      finallyp = None
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(TryBlock),context = eqBlockRenderContext(BlockRenderContext()))

    tryWithHandlerRenderer.render(tryWithHandler)

    outputWriter.toString shouldBe
      """try {
        |  doSomething();
        |}
        |/* UNPARSEABLE catch handler: someCatchHandler */
        |""".stripMargin
  }


  test("render with a 'finally'") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryBlock,
      catchp = CatchHandler,
      finallyp = Some(FinallyBlock)
    )

    doWrite(
      """ {
        |  doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(TryBlock), context = eqBlockRenderContext(BlockRenderContext()))

    doWrite(
      """finally {
        |  cleanup();
        |}
        |""".stripMargin)
      .when(finallyRenderer).render(eqTree(FinallyBlock))

    tryWithHandlerRenderer.render(tryWithHandler)

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

  test("render with a 'finally', and 'expr' has uncertainReturn=true") {
    val tryWithHandler = Term.TryWithHandler(
      expr = TryBlock,
      catchp = CatchHandler,
      finallyp = Some(FinallyBlock)
    )

    val exprContext = BlockRenderContext(lastStatContext = SimpleBlockStatRenderContext(uncertainReturn = true))
    val tryContext = TryRenderContext(exprContext = exprContext, catchContexts = List(exprContext))

    doWrite(
      """ {
        |  /* return? */doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(
      block = eqTree(TryBlock),context = eqBlockRenderContext(exprContext))

    doWrite(
      """finally {
        |  cleanup();
        |}
        |""".stripMargin)
      .when(finallyRenderer).render(eqTree(FinallyBlock))

    tryWithHandlerRenderer.render(tryWithHandler = tryWithHandler, context = tryContext)

    outputWriter.toString shouldBe
      """try {
        |  /* return? */doSomething();
        |}
        |/* UNPARSEABLE catch handler: someCatchHandler */
        |finally {
        |  cleanup();
        |}
        |""".stripMargin
  }
}
