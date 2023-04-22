package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pat, Term}

class PatVarRendererImplTest extends UnitTestSuite {

  private val termNameRenderer = mock[TermNameRenderer]

  private val patVarRenderer = new PatVarRendererImpl(termNameRenderer)

  test("traverse()") {
    val termName = Term.Name("x")

    patVarRenderer.render(Pat.Var(termName))

    verify(termNameRenderer).render(eqTree(termName))
  }
}
