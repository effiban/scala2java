package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, Lit, Pat}

class CaseTraverserImplTest extends UnitTestSuite {

  private val StringPat = Lit.String("value1")
  private val Cond = Lit.String("value2")
  private val Body: Lit.Int = Lit.Int(3)

  private val patTraverser = mock[PatTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val caseTraverser = new CaseTraverserImpl(
    patTraverser,
    expressionTermTraverser
  )


  test("traverse() non-default without condition") {
    doWrite(""""value1"""").when(patTraverser).traverse(eqTree(StringPat))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(
      Case(pat = StringPat,
        cond = None,
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """case "value1" -> 3;
        |""".stripMargin
  }

  test("traverse() default case without condition") {
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(
      Case(pat = Pat.Wildcard(),
        cond = None,
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """default -> 3;
        |""".stripMargin
  }

  test("traverse() with condition") {
    doWrite(""""value1"""").when(patTraverser).traverse(eqTree(StringPat))
    doWrite("x > 2").when(expressionTermTraverser).traverse(eqTree(Cond))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(
      Case(pat = StringPat,
        cond = Some(Cond),
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """case "value1" && x > 2 -> 3;
        |""".stripMargin
  }
}
