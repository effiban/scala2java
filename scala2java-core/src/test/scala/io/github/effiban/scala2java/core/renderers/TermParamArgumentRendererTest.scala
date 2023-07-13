package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTermParam

class TermParamArgumentRendererTest extends UnitTestSuite {

  private val termParamRenderer = mock[TermParamRenderer]
  private val argContext = mock[ArgumentContext]

  private val termParamArgumentRenderer = new TermParamArgumentRenderer(termParamRenderer)

  test("render()") {
    val termParam = param"x: Int"

    termParamArgumentRenderer.render(termParam, argContext)

    verify(termParamRenderer).render(eqTree(termParam))
  }
}