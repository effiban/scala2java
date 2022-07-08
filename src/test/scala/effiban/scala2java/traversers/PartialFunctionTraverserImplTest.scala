package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeMatcher.eqTree

import scala.meta.{Case, Pat, Term}

class PartialFunctionTraverserImplTest extends UnitTestSuite {

  private val termFunctionTraverser = mock[TermFunctionTraverser]

  private val partialFunctionTraverser = new PartialFunctionTraverserImpl(termFunctionTraverser)

  test("traverse") {

    val caseX = Case(pat = Pat.Var(Term.Name("x")), cond = None, body = Term.Name("X"))
    val caseY = Case(pat = Pat.Var(Term.Name("y")), cond = None, body = Term.Name("Y"))

    val partialFunction = Term.PartialFunction(List(caseX, caseY))

    val expectedDummyArgName = Term.Name("arg")
    val expectedTermFunction = Term.Function(
      params = List(Term.Param(mods = Nil, name = expectedDummyArgName, decltpe = None, default = None)),
      body = Term.Match(expr = expectedDummyArgName, cases = List(caseX, caseY))
    )

    partialFunctionTraverser.traverse(partialFunction)

    verify(termFunctionTraverser).traverse(eqTree(expectedTermFunction))
  }
}
