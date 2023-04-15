package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class TermNameRendererImplTest extends UnitTestSuite {

  private val termNameRenderer = new TermNameRendererImpl()

  test("traverse when transformer returns the same") {
    val termName = q"xyz"

    termNameRenderer.render(termName)

    outputWriter.toString shouldBe "xyz"
  }
}
