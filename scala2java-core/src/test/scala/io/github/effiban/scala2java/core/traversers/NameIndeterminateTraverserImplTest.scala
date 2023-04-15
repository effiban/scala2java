package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameIndeterminateRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Name

class NameIndeterminateTraverserImplTest extends UnitTestSuite {

  private val nameIndeterminateRenderer = mock[NameIndeterminateRenderer]

  private val nameIndeterminateTraverser = new NameIndeterminateTraverserImpl(nameIndeterminateRenderer)

  test("traverse()") {
    val nameIndeterminate = Name.Indeterminate("myName")
    nameIndeterminateTraverser.traverse(nameIndeterminate)
    verify(nameIndeterminateRenderer).render(eqTree(nameIndeterminate))
  }
}
