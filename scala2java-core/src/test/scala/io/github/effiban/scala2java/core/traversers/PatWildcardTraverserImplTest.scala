package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatWildcardRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Pat

class PatWildcardTraverserImplTest extends UnitTestSuite {

  private val patWildcardRenderer = mock[PatWildcardRenderer]
  private val patWildcardTraverser = new PatWildcardTraverserImpl(patWildcardRenderer)

  test("traverse()") {
    patWildcardTraverser.traverse(Pat.Wildcard())

    verify(patWildcardRenderer).render(eqTree(Pat.Wildcard()))
  }
}
