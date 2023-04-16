package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Pat

class PatWildcardRendererImplTest extends UnitTestSuite {

  val patWildcardRenderer = new PatWildcardRendererImpl()

  test("traverse()") {
    patWildcardRenderer.render(Pat.Wildcard())

    outputWriter.toString shouldBe "__"
  }
}
