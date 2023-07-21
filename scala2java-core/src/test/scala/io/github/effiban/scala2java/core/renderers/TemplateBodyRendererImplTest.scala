package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, TemplateBodyRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class TemplateBodyRendererImplTest extends UnitTestSuite {

  private val templateStatRenderer = mock[TemplateStatRenderer]

  private val templateBodyRenderer = new TemplateBodyRendererImpl(templateStatRenderer)

  test("render when has no stats") {
    templateBodyRenderer.render(Nil)

    outputWriter.toString shouldBe
      """ {
         |}
         |""".stripMargin
  }

  test("render when has stats") {
    val stat1 = q"private final var myConst: int = 3"
    val stat2 =
      q"""
      def myFunc(y: Int): Int = {
         y * 2
      }
      """
    val context = TemplateBodyRenderContext(
      Map(
        stat1 -> EmptyStatRenderContext,
        stat2 -> EmptyStatRenderContext
      )
    )

    doWrite(
      """  private final int myConst = 3;
        |""".stripMargin)
      .when(templateStatRenderer).render(eqTree(stat1), eqTo(EmptyStatRenderContext))
    doWrite(
      """  public int myFunc(final int y) {
        |    return y * 2;
        |  }
        |""".stripMargin)
      .when(templateStatRenderer).render(eqTree(stat2), eqTo(EmptyStatRenderContext))

    templateBodyRenderer.render(List(stat1, stat2), context)

    outputWriter.toString shouldBe
      """ {
        |  private final int myConst = 3;
        |  public int myFunc(final int y) {
        |    return y * 2;
        |  }
        |}
        |""".stripMargin
  }
}
