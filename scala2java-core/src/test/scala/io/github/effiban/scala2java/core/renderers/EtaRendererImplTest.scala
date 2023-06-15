package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term
import scala.meta.Term.Eta

class EtaRendererImplTest extends UnitTestSuite {

  private val expressionTermRenderer = mock[ExpressionTermRenderer]

  private val etaRenderer = new EtaRendererImpl(expressionTermRenderer)

  test("render()") {
    val methodName = Term.Name("myMethod")

    doWrite("myMethod").when(expressionTermRenderer).render(eqTree(methodName))

    etaRenderer.render(Eta(methodName))

    outputWriter.toString shouldBe "this::myMethod"
  }

}
