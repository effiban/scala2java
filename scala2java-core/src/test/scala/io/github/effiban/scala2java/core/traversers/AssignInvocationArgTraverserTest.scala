package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.matchers.ArgumentContextMatcher.eqArgumentContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Term}

class AssignInvocationArgTraverserTest extends UnitTestSuite {

  private val assignLHSTraverser = mock[AssignLHSTraverser]
  private val defaultInvocationArgTraverser = mock[InvocationArgTraverser[Term]]

  private val assignInvocationArgTraverser = new AssignInvocationArgTraverser(
    assignLHSTraverser,
    defaultInvocationArgTraverser
  )

  test("traverse when argNameAsComment = false") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)
    val initialContext = ArgumentContext(index = 0)
    val expectedAdjustedContext = ArgumentContext(maybeName = Some(lhs), index = 0)

    doWrite("x = ").when(assignLHSTraverser).traverse(eqTree(lhs), asComment = eqTo(false))
    doWrite("1").when(defaultInvocationArgTraverser).traverse(eqTree(rhs), eqArgumentContext(expectedAdjustedContext))

    assignInvocationArgTraverser.traverse(assign, initialContext)

    outputWriter.toString shouldBe "x = 1"
  }

  test("traverse when argNameAsComment = true") {
    val lhs = Term.Name("x")
    val rhs = Lit.Int(1)
    val assign = Term.Assign(lhs, rhs)
    val initialContext = ArgumentContext(index = 0, argNameAsComment = true)
    val expectedAdjustedContext = ArgumentContext(maybeName = Some(lhs), index = 0, argNameAsComment = true)

    doWrite("/* x = */").when(assignLHSTraverser).traverse(eqTree(lhs), asComment = eqTo(true))
    doWrite("1").when(defaultInvocationArgTraverser).traverse(eqTree(rhs), eqArgumentContext(expectedAdjustedContext))

    assignInvocationArgTraverser.traverse(assign, initialContext)

    outputWriter.toString shouldBe "/* x = */1"
  }
}
