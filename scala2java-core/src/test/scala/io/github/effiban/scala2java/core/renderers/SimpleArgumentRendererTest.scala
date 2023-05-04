package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Tree, XtensionQuasiquoteTerm}

class SimpleArgumentRendererTest extends UnitTestSuite {

  private val innerRenderer = mock[JavaTreeRenderer[Tree]]
  private val argContext = mock[ArgumentContext]

  private val simpleArgumentRenderer = new SimpleArgumentRenderer(innerRenderer)

  test("render()") {
    val arg = q"dummy"

    simpleArgumentRenderer.render(arg, argContext)

    verify(innerRenderer).render(arg)
  }
}
