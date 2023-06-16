package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Case, Pat, XtensionQuasiquoteTerm}

class DeprecatedCaseTraverserImplTest extends UnitTestSuite {

  private val StringPat = q"""value1A"""
  private val TraversedStringPat = q"""value1B"""
  private val Cond = q"""value2"""
  private val Body = q"3"

  private val patTraverser = mock[PatTraverser]
  private val patRenderer = mock[PatRenderer]
  private val expressionTermTraverser = mock[DeprecatedExpressionTermTraverser]

  private val caseTraverser = new DeprecatedCaseTraverserImpl(
    patTraverser,
    patRenderer,
    expressionTermTraverser
  )


  test("traverse() non-default without condition") {
    doReturn(TraversedStringPat).when(patTraverser).traverse(eqTree(StringPat))
    doWrite(""""value1B"""").when(patRenderer).render(eqTree(TraversedStringPat))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(
      Case(pat = StringPat,
        cond = None,
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """case "value1B" -> 3;
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
    doReturn(TraversedStringPat).when(patTraverser).traverse(eqTree(StringPat))
    doWrite(""""value1B"""").when(patRenderer).render(eqTree(TraversedStringPat))
    doWrite("x > 2").when(expressionTermTraverser).traverse(eqTree(Cond))
    doWrite("3").when(expressionTermTraverser).traverse(eqTree(Body))

    caseTraverser.traverse(
      Case(pat = StringPat,
        cond = Some(Cond),
        body = Body
      )
    )

    outputWriter.toString shouldBe
      """case "value1B" && x > 2 -> 3;
        |""".stripMargin
  }
}
