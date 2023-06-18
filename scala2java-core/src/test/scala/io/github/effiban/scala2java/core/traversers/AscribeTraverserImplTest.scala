package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class AscribeTraverserImplTest extends UnitTestSuite {
  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val typeTraverser = mock[TypeTraverser]

  private val ascribeTraverser = new AscribeTraverserImpl(
    expressionTermTraverser,
    typeTraverser
  )

  test("traverse") {
    val expr = q"2"
    val tpe = t"MyType"
    val ascribe = Term.Ascribe(expr, tpe)

    val traversedExpr = q"22"
    val traversedType = t"MyTraversedType"
    val traversedAscribe = Term.Ascribe(traversedExpr, traversedType)

    doReturn(traversedExpr).when(expressionTermTraverser).traverse(eqTree(expr))
    doReturn(traversedType).when(typeTraverser).traverse(eqTree(tpe))

    ascribeTraverser.traverse(ascribe).structure shouldBe traversedAscribe.structure
  }
}
