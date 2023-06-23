package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Case, Term, XtensionQuasiquoteTerm}

class TermMatchTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val caseTraverser = mock[CaseTraverser]

  private val termMatchTraverser = new TermMatchTraverserImpl(expressionTermTraverser, caseTraverser)

  test("traverse") {
    val expr = q"x"
    val traversedExpr = q"xx"
    
    val case1 = Case(pat = q"1", cond = None, body = q""""one"""")
    val case2 = Case(pat = q"2", cond = None, body = q""""two"""")

    val traversedCase1 = Case(pat = q"11", cond = None, body = q""""eleven"""")
    val traversedCase2 = Case(pat = q"22", cond = None, body = q""""twenty-two"""")
    
    val termMatch = Term.Match(
      expr = expr,
      cases = List(case1, case2),
      mods = Nil
    )

    val traversedTermMatch = Term.Match(
      expr = traversedExpr,
      cases = List(traversedCase1, traversedCase2),
      mods = Nil
    )
    
    doReturn(traversedExpr).when(expressionTermTraverser).traverse(eqTree(expr))
    doAnswer((`case`: Case) => `case` match {
      case aCase if aCase.structure == case1.structure => traversedCase1
      case aCase if aCase.structure == case2.structure => traversedCase2
      case aCase => aCase
    }).when(caseTraverser).traverse(any[Case])

    termMatchTraverser.traverse(termMatch).structure shouldBe traversedTermMatch.structure
  }
}
