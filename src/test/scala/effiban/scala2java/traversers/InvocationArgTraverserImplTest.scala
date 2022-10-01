package effiban.scala2java.traversers

import effiban.scala2java.contexts.InvocationArgContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Lit, Term}

class InvocationArgTraverserImplTest extends UnitTestSuite {

  private val assignTraverser = mock[AssignTraverser]
  private val termTraverser = mock[TermTraverser]

  private val invocationArgTraverser = new InvocationArgTraverserImpl(assignTraverser, termTraverser)

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
