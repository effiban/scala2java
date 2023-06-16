package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DeprecatedTermNameTraverserImplTest extends UnitTestSuite {

  private val termWithoutRenderTraverser = mock[DeprecatedTermNameWithoutRenderTraverser]
  private val termNameRenderer = mock[TermNameRenderer]

  private val termNameTraverser = new DeprecatedTermNameTraverserImpl(
    termWithoutRenderTraverser,
    termNameRenderer
  )

  test("traverse when inner traverser returns a Term.Name") {
    val termName = q"aa"
    val traversedTermName = q"bb"

    when(termWithoutRenderTraverser.traverse(eqTree(termName))).thenReturn(Some(traversedTermName))

    termNameTraverser.traverse(termName)

    verify(termNameRenderer).render(traversedTermName)
  }

  test("traverse when transformer returns None") {
    val termName = q"aa"

    when(termWithoutRenderTraverser.traverse(eqTree(termName))).thenReturn(None)

    termNameTraverser.traverse(termName)

    verifyZeroInteractions(termNameRenderer)
  }
}
