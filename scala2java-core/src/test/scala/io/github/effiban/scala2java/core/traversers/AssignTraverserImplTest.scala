package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term}

class AssignTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val expressionTraverser = mock[ExpressionTraverser]

  private val assignTraverser = new AssignTraverserImpl(termTraverser, expressionTraverser)

  test("traverse when LHS should be traversed normally") {
    val lhs = Term.Name("myVal")
    val rhs = Lit.Int(3)

    doWrite("myVal").when(termTraverser).traverse(eqTree(lhs))
    doWrite("3").when(expressionTraverser).traverse(eqTree(rhs))

    assignTraverser.traverse(assign = Term.Assign(lhs = lhs, rhs = rhs))

    outputWriter.toString shouldBe "myVal = 3"
  }

  test("traverse when LHS shuld be written as a comment") {
    val lhs = Term.Name("myVal")
    val rhs = Lit.Int(3)

    doWrite("3").when(expressionTraverser).traverse(eqTree(rhs))

    assignTraverser.traverse(assign = Term.Assign(lhs = lhs, rhs = rhs), lhsAsComment = true)

    outputWriter.toString shouldBe "/* myVal = */3"
  }
}
