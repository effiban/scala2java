package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.renderers.DefaultTermRefRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class FunTermRefTraverserTest extends UnitTestSuite {

  private val funTermSelectTraverser = mock[FunTermSelectTraverser]
  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]

  private val funTermRefTraverser = new FunTermRefTraverser(
    funTermSelectTraverser,
    defaultTermRefTraverser,
    defaultTermRefRenderer
  )

  test("traverse() for a Term.Select") {
    val termSelect = q"a.b"

    funTermRefTraverser.traverse(termSelect)

    verify(funTermSelectTraverser).traverse(eqTree(termSelect), eqTermSelectContext(TermSelectContext()))
  }

  test("traverse() for a Term.Name") {
    val termName = q"a"
    val traversedTermName = q"b"

    doReturn(traversedTermName).when(defaultTermRefTraverser).traverse(eqTree(termName))

    funTermRefTraverser.traverse(termName)

    verify(defaultTermRefRenderer).render(eqTree(traversedTermName))
  }
}
