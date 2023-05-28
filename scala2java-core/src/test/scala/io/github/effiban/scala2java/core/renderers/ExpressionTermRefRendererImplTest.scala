package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermRefRendererImplTest extends UnitTestSuite {

  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]

  private val expressionTermRefRenderer = new ExpressionTermRefRendererImpl(defaultTermRefRenderer)

  test("render() for Term.Name should call default renderer") {
    val termName = q"x"

    expressionTermRefRenderer.render(termName)

    verify(defaultTermRefRenderer).render(eqTree(termName))
  }

}
