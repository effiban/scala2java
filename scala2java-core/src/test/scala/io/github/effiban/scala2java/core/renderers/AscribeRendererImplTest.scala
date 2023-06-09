package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Lit, Term, XtensionQuasiquoteType}

class AscribeRendererImplTest extends UnitTestSuite {
  private val typeRenderer = mock[TypeRenderer]
  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val ascribeRenderer = new AscribeRendererImpl(
    typeRenderer,
    expressionTermRenderer
  )

  test("render") {
    val expr = Lit.Int(22)
    val typeName = t"MyType"

    doWrite("MyType").when(typeRenderer).render(eqTree(typeName))
    doWrite("22").when(expressionTermRenderer).render(eqTree(expr))

    ascribeRenderer.render(Term.Ascribe(expr = expr, tpe = typeName))

    outputWriter.toString shouldBe "(MyType)22"
  }
}
