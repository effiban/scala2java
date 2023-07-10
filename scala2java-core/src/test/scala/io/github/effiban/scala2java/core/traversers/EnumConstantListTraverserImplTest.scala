package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ArgumentListRendererImpl
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class EnumConstantListTraverserImplTest extends UnitTestSuite {

  private val enumConstantListTraverser = new EnumConstantListTraverserImpl(new ArgumentListRendererImpl())


  test("traverse() with one valid constant") {
    enumConstantListTraverser.traverse(q"final var One = Value")

    outputWriter.toString shouldBe "One"
  }

  test("traverse() with two valid constants") {
    enumConstantListTraverser.traverse(q"final var One, Two = Value")

    outputWriter.toString shouldBe "One, Two"
  }
}
