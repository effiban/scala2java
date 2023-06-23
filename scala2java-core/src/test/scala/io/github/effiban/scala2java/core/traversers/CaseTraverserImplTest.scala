package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, XtensionQuasiquoteTerm}

class CaseTraverserImplTest extends UnitTestSuite {

  private val StringPat = q"""value1A"""
  private val TraversedStringPat = q"""value1B"""
  private val Cond = q"""value2"""
  private val TraversedCond = q"""value22"""
  private val Body = q"3"
  private val TraversedBody = q"33"

  private val patTraverser = mock[PatTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val caseTraverser = new CaseTraverserImpl(
    patTraverser,
    expressionTermTraverser
  )


  test("traverse() without condition") {
    val `case` =
      Case(pat = StringPat,
        cond = None,
        body = Body
      )

    val traversedCase =
      Case(pat = TraversedStringPat,
        cond = None,
        body = TraversedBody
      )

    doReturn(TraversedStringPat).when(patTraverser).traverse(eqTree(StringPat))
    doReturn(TraversedBody).when(expressionTermTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(`case`).structure shouldBe traversedCase.structure
  }

  test("traverse() with condition") {
    val `case` =
      Case(pat = StringPat,
        cond = Some(Cond),
        body = Body
      )

    val traversedCase =
      Case(pat = TraversedStringPat,
        cond = Some(TraversedCond),
        body = TraversedBody
      )

    doReturn(TraversedStringPat).when(patTraverser).traverse(eqTree(StringPat))
    doReturn(TraversedCond).when(expressionTermTraverser).traverse(eqTree(Cond))
    doReturn(TraversedBody).when(expressionTermTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(`case`).structure shouldBe traversedCase.structure
  }
}
