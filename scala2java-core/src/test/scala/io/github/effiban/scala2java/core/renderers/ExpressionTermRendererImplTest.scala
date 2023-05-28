package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermRendererImplTest extends UnitTestSuite {

  private val defaultTermRenderer = mock[DefaultTermRenderer]

  private val expressionTermRenderer = new ExpressionTermRendererImpl(defaultTermRenderer)

  test("render() for Term.Apply should call default renderer") {
    val termApply = q"foo(3)"

    expressionTermRenderer.render(termApply)

    verify(defaultTermRenderer).render(eqTree(termApply))
  }
}
