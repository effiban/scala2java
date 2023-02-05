package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.entities.Decision.Uncertain
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Term, XtensionQuasiquoteTerm}

class InvocationArgTraverserTest extends UnitTestSuite {

  private val assignTraverser = mock[AssignTraverser]
  private val termFunctionTraverser = mock[TermFunctionTraverser]
  private val expressionTraverser = mock[ExpressionTraverser]

  private val invocationArgTraverser = new InvocationArgTraverser(
    assignTraverser,
    termFunctionTraverser,
    expressionTraverser
  )

  test("traverse when arg is a Lit") {
    val arg = Lit.Int(1)
    invocationArgTraverser.traverse(arg, ArgumentContext(index = 0))

    verify(expressionTraverser).traverse(eqTree(arg))
  }

  test("traverse when arg is an Assign and the default context") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)

    invocationArgTraverser.traverse(assign, ArgumentContext(index = 0))

    verify(assignTraverser).traverse(eqTree(assign), lhsAsComment = ArgumentMatchers.eq(false))
  }

  test("traverse when arg is an Assign and argNameAsComment = true") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)

    invocationArgTraverser.traverse(assign, context = ArgumentContext(index = 0, argNameAsComment = true))

    verify(assignTraverser).traverse(eqTree(assign), lhsAsComment = ArgumentMatchers.eq(true))
  }

  test("traverse when arg is a Block") {
    val block =
    q"""
    {
       x == y
    }
    """

    invocationArgTraverser.traverse(block, context = ArgumentContext(index = 0))

    verify(termFunctionTraverser).traverse(eqTree(Term.Function(Nil, block)), shouldBodyReturnValue = eqTo(Uncertain))
  }
}
