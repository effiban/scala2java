package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.BlockRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class FinallyRendererImplTest extends UnitTestSuite {

  private val FinallyBlock =
    q"""
    {
      log(error)
    }
    """

  private val blockRenderer = mock[BlockRenderer]

  private val finallyRenderer = new FinallyRendererImpl(blockRenderer)

  test("render()") {
    doWrite(
      """ {
        |  log(error);
        |}
        |""".stripMargin)
      .when(blockRenderer).render(
      block = eqTree(FinallyBlock),
      context = eqTo(BlockRenderContext()))

    finallyRenderer.render(FinallyBlock)

    outputWriter.toString shouldBe
      """finally {
        |  log(error);
        |}
        |""".stripMargin
  }
}
