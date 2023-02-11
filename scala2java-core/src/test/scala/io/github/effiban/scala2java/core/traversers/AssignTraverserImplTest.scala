package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Lit, Term}

class AssignTraverserImplTest extends UnitTestSuite {

  private val assignLHSTraverser = mock[AssignLHSTraverser]
  private val expressionTraverser = mock[ExpressionTraverser]

  private val assignTraverser = new AssignTraverserImpl(assignLHSTraverser, expressionTraverser)

  test("traverse()") {
    val lhs = Term.Name("myVal")
    val rhs = Lit.Int(3)

    doWrite("myVal = ").when(assignLHSTraverser).traverse(eqTree(lhs), asComment = eqTo(false))
    doWrite("3").when(expressionTraverser).traverse(eqTree(rhs))

    assignTraverser.traverse(assign = Term.Assign(lhs = lhs, rhs = rhs))

    outputWriter.toString shouldBe "myVal = 3"
  }
}
