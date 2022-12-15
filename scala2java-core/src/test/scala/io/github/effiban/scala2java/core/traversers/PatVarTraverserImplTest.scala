package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pat, Term}

class PatVarTraverserImplTest extends UnitTestSuite {

  private val termNameTraverser = mock[TermNameTraverser]

  private val patVarTraverser = new PatVarTraverserImpl(termNameTraverser)

  test("traverse()") {
    val termName = Term.Name("x")

    patVarTraverser.traverse(Pat.Var(termName))

    verify(termNameTraverser).traverse(eqTree(termName))
  }
}
