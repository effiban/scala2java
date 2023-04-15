package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.LitRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Lit

class LitTraverserImplTest extends UnitTestSuite {

  private val litRenderer = mock[LitRenderer]

  private val litTraverser = new LitTraverserImpl(litRenderer)

  test("traverse") {
    val lit = Lit.Int(3)

    litTraverser.traverse(lit)
    verify(litRenderer).render(eqTree(lit))
  }
}
