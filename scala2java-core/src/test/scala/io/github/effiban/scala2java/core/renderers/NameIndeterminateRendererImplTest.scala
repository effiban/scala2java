package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Name

class NameIndeterminateRendererImplTest extends UnitTestSuite {

  val nameIndeterminateTraverser = new NameIndeterminateRendererImpl()

  test("render()") {
    nameIndeterminateTraverser.render(Name.Indeterminate("myName"))
    outputWriter.toString shouldBe "myName"
  }
}
