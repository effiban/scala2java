package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermPlaceholderRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class TermPlaceholderTraverserImplTest extends UnitTestSuite {

  private val termPlaceholderRenderer = mock[TermPlaceholderRenderer]

  private val termPlaceholderTraverser = new TermPlaceholderTraverserImpl(termPlaceholderRenderer)

  test("render()") {
    termPlaceholderTraverser.traverse(Term.Placeholder())

    verify(termPlaceholderRenderer).render(eqTree(Term.Placeholder()))
  }

}
