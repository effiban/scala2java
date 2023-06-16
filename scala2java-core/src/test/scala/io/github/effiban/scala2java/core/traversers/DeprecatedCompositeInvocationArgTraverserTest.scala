package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.matchers.ArgumentContextMatcher.eqArgumentContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Assign
import scala.meta.{Lit, Term}

class DeprecatedCompositeInvocationArgTraverserTest extends UnitTestSuite {

  private val assignInvocationArgTraverser = mock[DeprecatedInvocationArgTraverser[Assign]]
  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]

  private val compositeInvocationArgTraverser = new DeprecatedCompositeInvocationArgTraverser(
    assignInvocationArgTraverser,
    expressionTermTraverser
  )

  test("traverse when arg is an Assign") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)
    val context = ArgumentContext()

    compositeInvocationArgTraverser.traverse(assign, context)

    verify(assignInvocationArgTraverser).traverse(eqTree(assign), eqArgumentContext(context))
  }

  test("traverse when arg is a Lit") {
    val arg = Lit.Int(1)
    val context = ArgumentContext()

    compositeInvocationArgTraverser.traverse(arg, context)

    verify(expressionTermTraverser).traverse(eqTree(arg))
  }

}
