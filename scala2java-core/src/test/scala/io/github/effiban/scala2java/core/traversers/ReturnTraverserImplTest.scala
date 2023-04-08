package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term
import scala.meta.Term.Return

class ReturnTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val returnTraverser = new ReturnTraverserImpl(expressionTermTraverser)

  test("traverse()") {
    val x = Term.Name("x")

    doWrite("x").when(expressionTermTraverser).traverse(eqTree(x))

    returnTraverser.traverse(Return(x))

    outputWriter.toString shouldBe "return x"
  }
}
