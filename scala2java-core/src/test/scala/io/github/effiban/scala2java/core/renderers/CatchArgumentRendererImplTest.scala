package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteCaseOrPattern

class CatchArgumentRendererImplTest extends UnitTestSuite {

  private val patRenderer = mock[PatRenderer]

  private val catchArgumentRenderer = new CatchArgumentRendererImpl(patRenderer)

  test("render when arg is a Pat.Typed") {
    val catchArg = p"e: IllegalStateException"

    doWrite("IllegalStateException e").when(patRenderer).render(eqTree(catchArg))

    catchArgumentRenderer.render(catchArg)

    outputWriter.toString shouldBe "(IllegalStateException e)"
  }

  test("render when arg is a Pat.Var") {
    val catchArg = p"e"

    catchArgumentRenderer.render(catchArg)

    outputWriter.toString shouldBe "(/* UNSUPPORTED catch argument: e */)"
  }
}
