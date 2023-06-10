package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{TermParamListRenderContext, TermParamRenderContext}
import io.github.effiban.scala2java.core.entities.JavaModifier.Final
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class TermParamArgRendererFactoryImplTest extends UnitTestSuite {

  private val termParamRenderer = mock[TermParamRenderer]

  private val termParamArgRendererFactory = new TermParamArgRendererFactoryImpl(termParamRenderer)

  test("create() for index 0") {
    val paramContext1 = TermParamRenderContext(javaModifiers = List(Final))
    val paramContext2 = TermParamRenderContext()

    val paramListContext = TermParamListRenderContext(paramContexts = List(paramContext1, paramContext2))

    val termParamArgumentRenderer = termParamArgRendererFactory.create(paramListContext, 0)
    termParamArgumentRenderer shouldBe a[TermParamArgumentRenderer]
    termParamArgumentRenderer.asInstanceOf[TermParamArgumentRenderer].termParamRenderContext shouldBe paramContext1
  }

  test("create() for index 1") {
    val paramContext1 = TermParamRenderContext(javaModifiers = List(Final))
    val paramContext2 = TermParamRenderContext()

    val paramListContext = TermParamListRenderContext(paramContexts = List(paramContext1, paramContext2))

    val termParamArgumentRenderer = termParamArgRendererFactory.create(paramListContext, 1)
    termParamArgumentRenderer shouldBe a[TermParamArgumentRenderer]
    termParamArgumentRenderer.asInstanceOf[TermParamArgumentRenderer].termParamRenderContext shouldBe paramContext2
  }
}
