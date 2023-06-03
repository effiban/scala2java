package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermRendererImplTest extends UnitTestSuite {

  private val expressionTermRefRenderer = mock[ExpressionTermRefRenderer]
  private val ifRenderer = mock[IfRenderer]
  private val defaultTermRenderer = mock[DefaultTermRenderer]

  private val expressionTermRenderer = new ExpressionTermRendererImpl(
    expressionTermRefRenderer,
    ifRenderer,
    defaultTermRenderer
  )

  test("render() for Term.Name should call ExpressionTermRefRenderer") {
    val termName = q"x"
    expressionTermRenderer.render(termName)
    verify(expressionTermRefRenderer).render(eqTree(termName))
  }

  test("render() for Term.Select should call ExpressionTermRefRenderer") {
    val termSelect = q"x.y"
    expressionTermRenderer.render(termSelect)
    verify(expressionTermRefRenderer).render(eqTree(termSelect))
  }

  test("render() for Term.This should call ExpressionTermRefRenderer") {
    val termThis = q"this"
    expressionTermRenderer.render(termThis)
    verify(expressionTermRefRenderer).render(eqTree(termThis))
  }

  test("render() for Term.Super should call ExpressionTermRefRenderer") {
    val termSuper = q"super"
    expressionTermRenderer.render(termSuper)
    verify(expressionTermRefRenderer).render(eqTree(termSuper))
  }

  test("render() for Term.ApplyUnary should call ExpressionTermRefRenderer") {
    val applyUnary = q"!x"
    expressionTermRenderer.render(applyUnary)
    verify(expressionTermRefRenderer).render(eqTree(applyUnary))
  }

  test("render() for Term.If should call IfRenderer.renderAsTertiaryOp()") {
    val termIf =
      q"""
      if (cond) {
        doSomething()
      } else {
        doSomethingElse()
      }
      """

    expressionTermRenderer.render(termIf)
    verify(ifRenderer).renderAsTertiaryOp(eqTree(termIf))
  }

  test("render() for Term.Apply should call default renderer") {
    val termApply = q"foo(3)"
    expressionTermRenderer.render(termApply)
    verify(defaultTermRenderer).render(eqTree(termApply))
  }
}
