package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term

class DeprecatedApplyUnaryTraverserImplTest extends UnitTestSuite {

  private val termNameRenderer = mock[TermNameRenderer]
  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]

  private val applyUnaryTraverser = new DeprecatedApplyUnaryTraverserImpl(termNameRenderer, expressionTermTraverser)

  test("traverse") {
    val op = Term.Name("!")
    val arg = Term.Name("myFlag")

    doWrite("!").when(termNameRenderer).render(eqTree(op))
    doWrite("myFlag").when(expressionTermTraverser).traverse(eqTree(arg))

    applyUnaryTraverser.traverse(Term.ApplyUnary(op = op, arg = arg))

    outputWriter.toString shouldBe "!myFlag"
  }
}
