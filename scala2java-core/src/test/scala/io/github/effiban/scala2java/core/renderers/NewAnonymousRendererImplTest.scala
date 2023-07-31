package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, TemplateBodyRenderContext, TemplateRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.TemplateRenderContextMockitoMatcher.eqTemplateRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Stat, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class NewAnonymousRendererImplTest extends UnitTestSuite {

  private val templateRenderer = mock[TemplateRenderer]

  private val newAnonymousRenderer = new NewAnonymousRendererImpl(templateRenderer)

  test("render()") {
    val stat1 = q"override def method1(x: Int) = x + 1"
    val stat2 = q"override def method2(x: Int) = x + 2"

    val template =
      template"""
      MyTrait {
        override def method1(x: Int) = x + 1
        override def method2(x: Int) = x + 2
      }
      """
    val newAnonymous =
      q"""
      new MyTrait {
        override def method1(x: Int) = x + 1
        override def method2(x: Int) = x + 2
      }
      """

    val expectedStatContextMap = Map[Stat, TemplateStatRenderContext](
      stat1 -> EmptyStatRenderContext,
      stat2 -> EmptyStatRenderContext
    )
    val expectedTemplateContext = TemplateRenderContext(
      renderInitArgs = true,
      bodyContext = TemplateBodyRenderContext(expectedStatContextMap)
    )

    doWrite(
      """ MyTrait() {
        |  public int method1(final int x) {
        |    return x + 1;
        |  }
        |  public int method2(final int x) {
        |    return x + 2;
        |  }
        |}
        |""".stripMargin)
      .when(templateRenderer).render(eqTree(template), eqTemplateRenderContext(expectedTemplateContext))

    newAnonymousRenderer.render(newAnonymous)

    outputWriter.toString shouldBe
      """new MyTrait() {
        |  public int method1(final int x) {
        |    return x + 1;
        |  }
        |  public int method2(final int x) {
        |    return x + 2;
        |  }
        |}
        |""".stripMargin
  }
}
