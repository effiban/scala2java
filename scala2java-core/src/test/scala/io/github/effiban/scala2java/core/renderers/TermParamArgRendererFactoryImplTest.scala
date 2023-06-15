package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{TermParamListRenderContext, TermParamRenderContext}
import io.github.effiban.scala2java.core.entities.JavaModifier.Final
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class TermParamArgRendererFactoryImplTest extends UnitTestSuite {

  private val termParamRenderer = mock[TermParamRenderer]

  private val termParamArgRendererFactory = new TermParamArgRendererFactoryImpl(termParamRenderer)

  test("apply()") {
    val paramContext = TermParamRenderContext(javaModifiers = List(Final))

    val paramListContext = TermParamListRenderContext(javaModifiers = paramContext.javaModifiers)

    val termParamArgumentRenderer = termParamArgRendererFactory(paramListContext)
    termParamArgumentRenderer shouldBe a[TermParamArgumentRenderer]
    termParamArgumentRenderer.asInstanceOf[TermParamArgumentRenderer].termParamRenderContext shouldBe paramContext
  }
}