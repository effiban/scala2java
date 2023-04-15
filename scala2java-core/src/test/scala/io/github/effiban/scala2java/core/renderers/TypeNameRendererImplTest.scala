package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteType

class TypeNameRendererImplTest extends UnitTestSuite {

  private val typeNameRenderer = new TypeNameRendererImpl()

  test("render") {
    val typeName = t"Optional"

    typeNameRenderer.render(typeName)

    outputWriter.toString shouldBe "Optional"
  }
}
