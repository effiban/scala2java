package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Throw
import scala.meta.{Init, Name, Term, Type}

class ThrowRendererImplTest extends UnitTestSuite {

  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val throwRenderer = new ThrowRendererImpl(expressionTermRenderer)

  test("render") {
    val exception = Term.New(Init(tpe = Type.Name("IllegalStateException"), name = Name.Anonymous(), argss = Nil))
    val `throw` = Throw(exception)

    throwRenderer.render(`throw`)

    verify(expressionTermRenderer).render(eqTree(exception))
  }
}
