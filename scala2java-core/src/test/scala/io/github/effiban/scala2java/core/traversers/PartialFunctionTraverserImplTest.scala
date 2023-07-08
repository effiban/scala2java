package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.Decision.No
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.TermFunctionTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Case, Term, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class PartialFunctionTraverserImplTest extends UnitTestSuite {

  private val CaseX = Case(pat = p"x", cond = None, body = q"X")
  private val CaseXX = Case(pat = p"xx", cond = None, body = q"XX")

  private val CaseY = Case(pat = p"y", cond = None, body = q"Y")
  private val CaseYY = Case(pat = p"yy", cond = None, body = q"YY")

  private val ThePartialFunction = Term.PartialFunction(List(CaseX, CaseY))

  private val TheFunction = Term.Function(
    params = List(Term.Param(mods = Nil, name = q"arg", decltpe = None, default = None)),
    body = Term.Match(expr = q"arg", cases = List(CaseX, CaseY))
  )
  private val TheTraversedFunction = Term.Function(
    params = List(Term.Param(mods = Nil, name = q"traversedArg", decltpe = None, default = None)),
    body = Term.Match(expr = q"traversedArg", cases = List(CaseXX, CaseYY))
  )

  private val termFunctionTraverser = mock[TermFunctionTraverser]

  private val partialFunctionTraverser = new PartialFunctionTraverserImpl(termFunctionTraverser)

  test("traverse()") {
    val expectedResult = TermFunctionTraversalResult(TheTraversedFunction)

    doReturn(expectedResult).when(termFunctionTraverser).traverse(eqTree(TheFunction), shouldBodyReturnValue = eqTo(No))

    partialFunctionTraverser.traverse(ThePartialFunction).function.structure shouldBe TheTraversedFunction.structure
  }
}
