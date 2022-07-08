package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeMatcher.eqTree

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
