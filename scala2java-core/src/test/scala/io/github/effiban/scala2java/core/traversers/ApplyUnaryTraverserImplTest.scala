package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class ApplyUnaryTraverserImplTest extends UnitTestSuite {

  private val termNameTraverser = mock[TermNameTraverser]
  private val termTraverser = mock[TermTraverser]

  private val applyUnaryTraverser = new ApplyUnaryTraverserImpl(termNameTraverser, termTraverser)

  test("traverse") {
    val op = Term.Name("!")
    val arg = Term.Name("myFlag")

    doWrite("!").when(termNameTraverser).traverse(eqTree(op))
    doWrite("myFlag").when(termTraverser).traverse(eqTree(arg))

    applyUnaryTraverser.traverse(Term.ApplyUnary(op = op, arg = arg))

    outputWriter.toString shouldBe "!myFlag"
  }
}
