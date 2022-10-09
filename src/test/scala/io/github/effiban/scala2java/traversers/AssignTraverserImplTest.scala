package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Lit, Term}

class AssignTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val rhsTermTraverser = mock[RhsTermTraverser]

  private val assignTraverser = new AssignTraverserImpl(termTraverser, rhsTermTraverser)

  test("traverse when LHS should be traversed normally") {
    val lhs = Term.Name("myVal")
    val rhs = Lit.Int(3)

    doWrite("myVal").when(termTraverser).traverse(eqTree(lhs))
    doWrite("3").when(rhsTermTraverser).traverse(eqTree(rhs))

    assignTraverser.traverse(assign = Term.Assign(lhs = lhs, rhs = rhs))

    outputWriter.toString shouldBe "myVal = 3"
  }

  test("traverse when LHS shuld be written as a comment") {
    val lhs = Term.Name("myVal")
    val rhs = Lit.Int(3)

    doWrite("3").when(rhsTermTraverser).traverse(eqTree(rhs))

    assignTraverser.traverse(assign = Term.Assign(lhs = lhs, rhs = rhs), lhsAsComment = true)

    outputWriter.toString shouldBe "/* myVal = */3"
  }
}
