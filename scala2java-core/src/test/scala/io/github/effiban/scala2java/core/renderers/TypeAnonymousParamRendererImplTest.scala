package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Mod, Type}

class TypeAnonymousParamRendererImplTest extends UnitTestSuite {

  private val typeAnonymousParamRenderer = new TypeAnonymousParamRendererImpl()

  test("render") {
    typeAnonymousParamRenderer.render(Type.AnonymousParam(Some(Mod.Covariant())))

    outputWriter.toString shouldBe "?"
  }

}
