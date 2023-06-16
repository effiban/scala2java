package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, Lit, Term}

class DeprecatedTermMatchTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]
  private val caseTraverser = mock[DeprecatedCaseTraverser]

  private val termMatchTraverser = new DeprecatedTermMatchTraverserImpl(expressionTermTraverser, caseTraverser)

  test("traverse") {
    val expr = Term.Name("x")
    val case1 = Case(pat = Lit.Int(1), cond = None, body = Lit.String("one"))
    val case2 = Case(pat = Lit.Int(2), cond = None, body = Lit.String("two"))

    val termMatch = Term.Match(
      expr = expr,
      cases = List(case1, case2),
      mods = Nil
    )
    doWrite("x").when(expressionTermTraverser).traverse(eqTree(expr))
    doWrite(
      """  case 1 -> "one";
        |""".stripMargin).when(caseTraverser).traverse(eqTree(case1))
    doWrite(
      """  case 2 -> "two";
        |""".stripMargin).when(caseTraverser).traverse(eqTree(case2))

    termMatchTraverser.traverse(termMatch)

    outputWriter.toString shouldBe
    """switch (x) {
        |  case 1 -> "one";
        |  case 2 -> "two";
        |}
        |""".stripMargin
  }
}
