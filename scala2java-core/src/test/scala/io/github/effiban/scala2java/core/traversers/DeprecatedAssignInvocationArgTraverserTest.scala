package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Term}

class DeprecatedAssignInvocationArgTraverserTest extends UnitTestSuite {

  private val assignLHSTraverser = mock[DeprecatedAssignLHSTraverser]
  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]

  private val assignInvocationArgTraverser = new DeprecatedAssignInvocationArgTraverser(
    assignLHSTraverser,
    expressionTermTraverser
  )

  test("traverse when argNameAsComment = false") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)
    val context = ArgumentContext()

    doWrite("x = ").when(assignLHSTraverser).traverse(eqTree(lhs), asComment = eqTo(false))
    doWrite("1").when(expressionTermTraverser).traverse(eqTree(rhs))

    assignInvocationArgTraverser.traverse(assign, context)

    outputWriter.toString shouldBe "x = 1"
  }

  test("traverse when argNameAsComment = true") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)
    val context = ArgumentContext(argNameAsComment = true)

    doWrite("/* x = */").when(assignLHSTraverser).traverse(eqTree(lhs), asComment = eqTo(true))
    doWrite("1").when(expressionTermTraverser).traverse(eqTree(rhs))

    assignInvocationArgTraverser.traverse(assign, context)

    outputWriter.toString shouldBe "/* x = */1"
  }
}
