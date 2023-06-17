package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.Term.Assign
import scala.meta.{Term, XtensionQuasiquoteTerm}

class AssignTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val assignTraverser = new AssignTraverserImpl(expressionTermTraverser)

  test("traverse()") {
    val lhs = q"myVal"
    val rhs = q"3"
    val assign = Assign(lhs, rhs)

    val traversedLhs = q"myOtherVal"
    val traversedRhs = q"4"
    val traversedAssign = Assign(traversedLhs, traversedRhs)

    doAnswer((term: Term) => term match {
      case aTerm if aTerm.structure == lhs.structure => traversedLhs
      case aTerm if aTerm.structure == rhs.structure => traversedRhs
      case aTerm => aTerm
    }).when(expressionTermTraverser).traverse(any[Term])

    assignTraverser.traverse(assign).structure shouldBe traversedAssign.structure
  }
}
