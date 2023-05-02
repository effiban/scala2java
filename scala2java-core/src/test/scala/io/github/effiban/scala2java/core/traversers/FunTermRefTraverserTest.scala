package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class FunTermRefTraverserTest extends UnitTestSuite {

  private val funTermSelectTraverser = mock[FunTermSelectTraverser]
  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]

  private val funTermRefTraverser = new FunTermRefTraverser(funTermSelectTraverser, defaultTermRefTraverser)

  test("traverse() for a Term.Select") {
    val termSelect = q"a.b"
    val context = TermSelectContext(List(t"T"))

    funTermRefTraverser.traverse(termSelect)

    verify(funTermSelectTraverser).traverse(eqTree(termSelect), eqTermSelectContext(TermSelectContext()))
  }

  test("traverse() for a Term.Name") {
    val termName = q"a"

    funTermRefTraverser.traverse(termName)

    verify(defaultTermRefTraverser).traverse(eqTree(termName))
  }
}
