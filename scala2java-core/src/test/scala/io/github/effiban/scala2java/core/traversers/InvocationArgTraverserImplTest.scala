package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.InvocationArgContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Lit, Term}

class InvocationArgTraverserImplTest extends UnitTestSuite {

  private val assignTraverser = mock[AssignTraverser]
  private val termFunctionTraverser = mock[TermFunctionTraverser]
  private val termTraverser = mock[TermTraverser]

  private val invocationArgTraverser = new InvocationArgTraverserImpl(
    assignTraverser,
    termFunctionTraverser,
    termTraverser
  )

  test("traverse when arg is a Lit") {
    val arg = Lit.Int(1)
    invocationArgTraverser.traverse(arg)

    verify(termTraverser).traverse(eqTree(arg))
  }

  test("traverse when arg is an Assign and the default context") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)

    invocationArgTraverser.traverse(assign)

    verify(assignTraverser).traverse(eqTree(assign), lhsAsComment = ArgumentMatchers.eq(false))
  }

  test("traverse when arg is an Assign and argNameAsComment = true") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)

    invocationArgTraverser.traverse(assign, context = InvocationArgContext(argNameAsComment = true))

    verify(assignTraverser).traverse(eqTree(assign), lhsAsComment = ArgumentMatchers.eq(true))
  }
}
