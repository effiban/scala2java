package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Case, Lit, Term}

class TermMatchTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val caseTraverser = mock[CaseTraverser]

  private val termMatchTraverser = new TermMatchTraverserImpl(termTraverser, caseTraverser)

  test("traverse") {
    val expr = Term.Name("x")
    val case1 = Case(pat = Lit.Int(1), cond = None, body = Lit.String("one"))
    val case2 = Case(pat = Lit.Int(2), cond = None, body = Lit.String("two"))

    val termMatch = Term.Match(
      expr = expr,
      cases = List(case1, case2),
      mods = Nil
    )
    doWrite("x").when(termTraverser).traverse(eqTree(expr))
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