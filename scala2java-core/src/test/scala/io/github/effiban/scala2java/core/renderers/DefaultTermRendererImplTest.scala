package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultTermRendererImplTest extends UnitTestSuite {

  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val litRenderer = mock[LitRenderer]

  private val defaultTermRenderer = new DefaultTermRendererImpl(
    defaultTermRefRenderer,
    litRenderer
  )

  test("render termName") {
    val termName = q"x"

    defaultTermRenderer.render(termName)

    verify(defaultTermRefRenderer).render(eqTree(termName))
  }

  test("render lit") {
    val lit = q"3"

    defaultTermRenderer.render(lit)

    verify(litRenderer).render(eqTree(lit))
  }
}
