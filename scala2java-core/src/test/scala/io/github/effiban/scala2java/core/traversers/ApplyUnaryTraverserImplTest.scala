package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class ApplyUnaryTraverserImplTest extends UnitTestSuite {

  private val defaultTermNameTraverser = mock[TermNameTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val applyUnaryTraverser = new ApplyUnaryTraverserImpl(defaultTermNameTraverser, expressionTermTraverser)

  test("traverse") {
    val op = Term.Name("!")
    val arg = Term.Name("myFlag")

    doWrite("!").when(defaultTermNameTraverser).traverse(eqTree(op))
    doWrite("myFlag").when(expressionTermTraverser).traverse(eqTree(arg))

    applyUnaryTraverser.traverse(Term.ApplyUnary(op = op, arg = arg))

    outputWriter.toString shouldBe "!myFlag"
  }
}
