package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Term, Type}

class PermittedSubTypeNameListRendererImplTest extends UnitTestSuite {

  private val permittedSubTypeNameListRenderer = new PermittedSubTypeNameListRendererImpl(new ArgumentListRendererImpl())

  test("render() when one") {
    val permittedSubTypeName = Type.Name("A")

    permittedSubTypeNameListRenderer.render(List(permittedSubTypeName))

    outputWriter.toString shouldBe "permits A"
  }

  test("render() when two") {
    val permittedSubTypeNames = List(Type.Name("A"), Term.Name("B"))

    permittedSubTypeNameListRenderer.render(permittedSubTypeNames)

    outputWriter.toString shouldBe "permits A, B"
  }
}
