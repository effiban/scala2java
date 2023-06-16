package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term
import scala.meta.Term.Eta

class DeprecatedEtaTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]

  private val etaTraverser = new DeprecatedEtaTraverserImpl(expressionTermTraverser)

  test("traverse()") {
    val methodName = Term.Name("myMethod")

    doWrite("myMethod").when(expressionTermTraverser).traverse(eqTree(methodName))

    etaTraverser.traverse(Eta(methodName))

    outputWriter.toString shouldBe "this::myMethod"
  }

}
