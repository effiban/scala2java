package effiban.scala2java.traversers

import effiban.scala2java.entities.Decision.{Uncertain, Yes}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Case, Pat, Term}

class PartialFunctionTraverserImplTest extends UnitTestSuite {

  private val termFunctionTraverser = mock[TermFunctionTraverser]

  private val partialFunctionTraverser = new PartialFunctionTraverserImpl(termFunctionTraverser)

  test("traverse()") {

    val caseX = Case(pat = Pat.Var(Term.Name("x")), cond = None, body = Term.Name("X"))
    val caseY = Case(pat = Pat.Var(Term.Name("y")), cond = None, body = Term.Name("Y"))

    val partialFunction = Term.PartialFunction(List(caseX, caseY))

    val expectedDummyArgName = Term.Name("arg")
    val expectedTermFunction = Term.Function(
      params = List(Term.Param(mods = Nil, name = expectedDummyArgName, decltpe = None, default = None)),
      body = Term.Match(expr = expectedDummyArgName, cases = List(caseX, caseY))
    )

    partialFunctionTraverser.traverse(partialFunction)

    verify(termFunctionTraverser).traverse(eqTree(expectedTermFunction), ArgumentMatchers.eq(Uncertain))
  }

  test("traverse() when shouldBodyReturnValue=Yes") {

    val caseX = Case(pat = Pat.Var(Term.Name("x")), cond = None, body = Term.Name("X"))
    val caseY = Case(pat = Pat.Var(Term.Name("y")), cond = None, body = Term.Name("Y"))

    val partialFunction = Term.PartialFunction(List(caseX, caseY))

    val expectedDummyArgName = Term.Name("arg")
    val expectedTermFunction = Term.Function(
      params = List(Term.Param(mods = Nil, name = expectedDummyArgName, decltpe = None, default = None)),
      body = Term.Match(expr = expectedDummyArgName, cases = List(caseX, caseY))
    )

    partialFunctionTraverser.traverse(partialFunction, shouldBodyReturnValue = Yes)

    verify(termFunctionTraverser).traverse(eqTree(expectedTermFunction), ArgumentMatchers.eq(Yes))
  }
}
