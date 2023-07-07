package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class StatTermRendererImplTest extends UnitTestSuite {
  private val expressionTermRefRenderer = mock[ExpressionTermRefRenderer]
  private val defaultTermRenderer = mock[DefaultTermRenderer]

  private val statTermRenderer = new StatTermRendererImpl(
    expressionTermRefRenderer,
    defaultTermRenderer)

  test("render() a Term.Name should call the ExpressionTermRefRenderer") {
    val termName = q"aa"

    statTermRenderer.render(termName)

    verify(expressionTermRefRenderer).render(eqTree(termName))
  }

  test("render() a Term.Select should call the ExpressionTermRefRenderer") {
    val termSelect = q"A.a"

    statTermRenderer.render(termSelect)

    verify(expressionTermRefRenderer).render(eqTree(termSelect))
  }

  test("render() a Term.ApplyType should call the DefaultTermRenderer") {
    val termApplyType = q"a[Type1]"

    statTermRenderer.render(termApplyType)

    verify(defaultTermRenderer).render(termApplyType)
  }

  test("render() when fun is a Term.Apply should call the DefaultTermRenderer") {
    val termApply = q"a(1)"

    statTermRenderer.render(termApply)

    verify(defaultTermRenderer).render(eqTree(termApply))
  }

  test("render() when fun is a Term.ApplyInfix should call the DefaultTermRenderer") {
    val termApplyInfix = q"a + b"

    statTermRenderer.render(termApplyInfix)

    verify(defaultTermRenderer).render(eqTree(termApplyInfix))
  }
}
