package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, InitContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteInit

class InitArgumentRendererTest extends UnitTestSuite {

  private val initRenderer = mock[InitRenderer]
  private val initContext = mock[InitContext]
  private val argContext = mock[ArgumentContext]

  private val initArgumentRenderer = new InitArgumentRenderer(initRenderer, initContext)

  test("render()") {
    val init = init"MyType()"
    initArgumentRenderer.render(init, argContext)

    verify(initRenderer).render(eqTree(init), eqTo(initContext))
  }
}
