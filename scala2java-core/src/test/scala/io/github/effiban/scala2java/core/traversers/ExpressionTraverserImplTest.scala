package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTraverserImplTest extends UnitTestSuite {

  private val ifTraverser = mock[IfTraverser]
  private val termTraverser = mock[TermTraverser]

  private val expressionTraverser = new ExpressionTraverserImpl(ifTraverser, termTraverser)

  test("traverse() for Term.Name") {
    val expression = q"abc"
    doWrite("abc").when(termTraverser).traverse(eqTree(expression))

    expressionTraverser.traverse(expression)

    outputWriter.toString shouldBe "abc"
  }

  test("traverse() for If") {
    val expression =
    q"""
    if (x == 3) {
      doSomething()
    } else {
      doNothing()
    }
    """
    doWrite("(x == 3) ? doSomething() : doNothing()")
      .when(ifTraverser).traverseAsTertiaryOp(eqTree(expression))

    expressionTraverser.traverse(expression)

    outputWriter.toString shouldBe "(x == 3) ? doSomething() : doNothing()"
  }
}
