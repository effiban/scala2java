package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term

class TermRepeatedTraverserImplTest extends UnitTestSuite {
  private val termTraverser = mock[TermTraverser]

  private val termRepeatedTraverser = new TermRepeatedTraverserImpl(termTraverser)

  test("traverse") {
    val expr = Term.Name("x")
    val termRepeated = Term.Repeated(expr)

    termRepeatedTraverser.traverse(termRepeated)

    verify(termTraverser).traverse(eqTree(expr))
  }
}
