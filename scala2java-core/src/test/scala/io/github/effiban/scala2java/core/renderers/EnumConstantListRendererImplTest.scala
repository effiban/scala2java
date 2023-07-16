package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class EnumConstantListRendererImplTest extends UnitTestSuite {

  private val enumConstantListRenderer = new EnumConstantListRendererImpl(new ArgumentListRendererImpl())


  test("render() with one valid constant") {
    enumConstantListRenderer.render(q"final var One = Value")

    outputWriter.toString shouldBe "One"
  }

  test("render() with two valid constants") {
    enumConstantListRenderer.render(q"final var One, Two = Value")

    outputWriter.toString shouldBe "One, Two"
  }
}
