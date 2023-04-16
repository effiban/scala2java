package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.Selfs

import scala.meta.{Name, Self, Type}

class SelfRendererImplTest extends UnitTestSuite {

  private val selfRenderer = new SelfRendererImpl()

  test("render when has a type") {
    val `self` = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(Type.Name("SelfType")))

    selfRenderer.render(`self`)

    outputWriter.toString shouldBe "/* extends SelfName: SelfType */"
  }

  test("render when empty") {
    selfRenderer.render(Selfs.Empty)

    outputWriter.toString shouldBe ""
  }
}
