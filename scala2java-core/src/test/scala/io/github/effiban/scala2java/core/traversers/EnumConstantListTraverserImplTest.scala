package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class EnumConstantListTraverserImplTest extends UnitTestSuite {

  private val enumConstantListTraverser = new EnumConstantListTraverserImpl(new DeprecatedArgumentListTraverserImpl())


  test("traverse() with one valid constant") {
    enumConstantListTraverser.traverse(q"val One = Value")

    outputWriter.toString shouldBe "One"
  }

  test("traverse() with two valid constants") {
    enumConstantListTraverser.traverse(q"val One, Two = Value")

    outputWriter.toString shouldBe "One, Two"
  }
}
