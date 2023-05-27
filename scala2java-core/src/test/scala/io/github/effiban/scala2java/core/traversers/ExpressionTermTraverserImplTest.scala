package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermTraverserImplTest extends UnitTestSuite {

  private val ifTraverser = mock[IfTraverser]
  private val statTraverser = mock[StatTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]
  private val expressionTermRefTraverser = mock[ExpressionTermRefTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]

  private val expressionTraverser = new ExpressionTermTraverserImpl(
    ifTraverser,
    statTraverser,
    termApplyTraverser,
    expressionTermRefTraverser,
    defaultTermTraverser
  )

  test("traverse() for Term.Name") {
    val expression = q"abc"

    doWrite("abc").when(expressionTermRefTraverser).traverse(eqTree(expression))

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

  test("traverse() for Block of one statement") {
    val block =
      q"""{
        x == 3
      }
      """
    val stat = q"x == 3"
    doWrite("x == 3").when(statTraverser).traverse(eqTree(stat), eqTo(StatContext()))

    expressionTraverser.traverse(block)

    outputWriter.toString shouldBe "x == 3"
  }

  test("traverse() for Block of two statements") {
    val block =
      q"""{
        val x = 3
        x + 1
      }
      """
    val expectedTermApply =
      q"""(() => {
        val x = 3
        x + 1
      }).apply()
      """

    doWrite(
      """((Supplier<Integer>)() -> {
        |  var x = 3;
        |  return x + 1;
        |}).get()
        |""".stripMargin)
      .when(termApplyTraverser).traverse(eqTree(expectedTermApply))

    expressionTraverser.traverse(block)

    outputWriter.toString shouldBe
      """((Supplier<Integer>)() -> {
        |  var x = 3;
        |  return x + 1;
        |}).get()
        |""".stripMargin
  }

  test("traverse() for Lit.Int") {
    val expression = q"3"
    doWrite("3").when(defaultTermTraverser).traverse(eqTree(expression))

    expressionTraverser.traverse(expression)

    outputWriter.toString shouldBe "3"
  }

}
