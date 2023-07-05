package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, SimpleBlockStatRenderContext}
import io.github.effiban.scala2java.core.matchers.BlockRenderContextMockitoMatcher.eqBlockRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class CatchHandlerRendererImplTest extends UnitTestSuite {

  private val CatchArg = p"e"
  private val RenderedCatchArg = "(RuntimeException e)"
  private val BlockOfCertainReturnStatement =
    q"""
    {
      return e.doSomething()
    }
    """

  private val BlockOfUncertainReturnStatement =
    q"""
    {
      e.doSomething()
    }
    """

  private val catchArgumentRenderer = mock[CatchArgumentRenderer]
  private val blockRenderer = mock[BlockRenderer]

  private val catchHandlerRenderer = new CatchHandlerRendererImpl(
    catchArgumentRenderer,
    blockRenderer
  )

  test("traverse() when body has uncertainReturn=false") {
    val CatchCase = Case(pat = CatchArg, cond = None, body = BlockOfCertainReturnStatement)

    doWrite(RenderedCatchArg).when(catchArgumentRenderer).render(eqTree(CatchArg))

    doWrite(
      """ {
        |  return e.doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(
      block = eqTree(BlockOfCertainReturnStatement),
      context = eqBlockRenderContext(BlockRenderContext())
    )

    catchHandlerRenderer.render(CatchCase)

    outputWriter.toString shouldBe
      """catch (RuntimeException e) {
        |  return e.doSomething();
        |}
        |""".stripMargin
  }

  test("traverse() when body has uncertainReturn=true") {
    val CatchCase = Case(pat = CatchArg, cond = None, body = BlockOfUncertainReturnStatement)

    val context = BlockRenderContext(lastStatContext = SimpleBlockStatRenderContext(uncertainReturn = true))

    doWrite(RenderedCatchArg).when(catchArgumentRenderer).render(eqTree(CatchArg))

    doWrite(
      """ {
        |  /* return? */e.doSomething();
        |}
        |""".stripMargin)
      .when(blockRenderer).render(
      block = eqTree(BlockOfUncertainReturnStatement),
      context = eqBlockRenderContext(context)
    )

    catchHandlerRenderer.render(CatchCase, context)

    outputWriter.toString shouldBe
      """catch (RuntimeException e) {
        |  /* return? */e.doSomething();
        |}
        |""".stripMargin
  }

}
