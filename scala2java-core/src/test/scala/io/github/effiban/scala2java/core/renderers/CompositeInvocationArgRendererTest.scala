package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.matchers.ArgumentContextMatcher.eqArgumentContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Assign
import scala.meta.{Term, XtensionQuasiquoteTerm}

class CompositeInvocationArgRendererTest extends UnitTestSuite {
  private val assignInvocationArgRenderer = mock[InvocationArgRenderer[Assign]]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val compositeInvocationArgRenderer = new CompositeInvocationArgRenderer(
    assignInvocationArgRenderer,
    expressionTermRenderer
  )

  test("render when arg is an Assign") {
    val lhs = q"x"
    val rhs = q"1"
    val assign = Term.Assign(lhs, rhs)
    val context = ArgumentContext()

    compositeInvocationArgRenderer.render(assign, context)

    verify(assignInvocationArgRenderer).render(eqTree(assign), eqArgumentContext(context))
  }

  test("render when arg is a Lit") {
    val arg = q"1"
    val context = ArgumentContext()

    compositeInvocationArgRenderer.render(arg, context)

    verify(expressionTermRenderer).render(eqTree(arg))
  }
}
