package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Term}

class InvocationArgTraverserTest extends UnitTestSuite {

  private val assignLHSTraverser = mock[AssignLHSTraverser]
  private val expressionTraverser = mock[ExpressionTraverser]

  private val invocationArgTraverser = new InvocationArgTraverser(
    assignLHSTraverser,
    expressionTraverser
  )

  test("traverse when arg is a Lit") {
    val arg = Lit.Int(1)
    invocationArgTraverser.traverse(arg, ArgumentContext(index = 0))

    verify(expressionTraverser).traverse(eqTree(arg))
  }

  test("traverse when arg is an Assign and argNameAsComment = false") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)

    doWrite("x = ").when(assignLHSTraverser).traverse(eqTree(lhs), asComment = eqTo(false))
    doWrite("1").when(expressionTraverser).traverse(eqTree(rhs))

    invocationArgTraverser.traverse(assign, ArgumentContext(index = 0))

    outputWriter.toString shouldBe "x = 1"
  }

  test("traverse when arg is an Assign and argNameAsComment = true") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)

    doWrite("/* x = */").when(assignLHSTraverser).traverse(eqTree(lhs), asComment = eqTo(true))
    doWrite("1").when(expressionTraverser).traverse(eqTree(rhs))

    invocationArgTraverser.traverse(assign, ArgumentContext(index = 0, argNameAsComment = true))

    outputWriter.toString shouldBe "/* x = */1"
  }
}
