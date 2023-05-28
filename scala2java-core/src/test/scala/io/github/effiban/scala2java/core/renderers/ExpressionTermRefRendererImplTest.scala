package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermRefRendererImplTest extends UnitTestSuite {

  private val expressionTermSelectRenderer = mock[ExpressionTermSelectRenderer]
  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]

  private val expressionTermRefRenderer = new ExpressionTermRefRendererImpl(
    expressionTermSelectRenderer,
    defaultTermRefRenderer
  )

  test("render() for Term.Select should call corresponding renderer") {
    val termSelect = q"x.y"

    expressionTermRefRenderer.render(termSelect)

    verify(expressionTermSelectRenderer).render(eqTree(termSelect), eqTermSelectContext(TermSelectContext()))
  }

  test("render() for Term.Name should call default renderer") {
    val termName = q"x"

    expressionTermRefRenderer.render(termName)

    verify(defaultTermRefRenderer).render(eqTree(termName))
  }

}
