package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Term.If
import scala.meta.{Term, XtensionQuasiquoteTerm}

class ExpressionIfTraverserImplTest extends UnitTestSuite {

  private val Condition = q"x < 3"
  private val TraversedCondition = q"xx < 3"

  private val ThenTerm = q"calcX(x)"
  private val TraversedThenTerm = q"calcXX(xx)"

  private val ElseTerm = q"otherCalcX(x)"
  private val TraversedElseTerm = q"otherCalcXX(xx)"

  private val TheIf = If(
    cond = Condition,
    thenp = ThenTerm,
    elsep = ElseTerm
  )

  private val TheTraversedIf = If(
    cond = TraversedCondition,
    thenp = TraversedThenTerm,
    elsep = TraversedElseTerm
  )

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val expressionIfTraverser = new ExpressionIfTraverserImpl(expressionTermTraverser)

  test("traverse") {

    doAnswer((term: Term) => term match {
      case aTerm if aTerm.structure == Condition.structure => TraversedCondition
      case aTerm if aTerm.structure == ThenTerm.structure => TraversedThenTerm
      case aTerm if aTerm.structure == ElseTerm.structure => TraversedElseTerm
    }).when(expressionTermTraverser).traverse(any[Term])

    expressionIfTraverser.traverse(TheIf).structure shouldBe TheTraversedIf.structure
  }
}
