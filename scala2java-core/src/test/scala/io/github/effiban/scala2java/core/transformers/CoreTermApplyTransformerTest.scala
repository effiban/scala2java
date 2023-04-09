package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class CoreTermApplyTransformerTest extends UnitTestSuite {

  private val coreTermApplyTransformer = new CoreTermApplyTransformer

  test("transform() of an unrecognized Term.Apply should return the same") {
    val termApply = q"blabla(1)"

    coreTermApplyTransformer.transform(termApply).structure shouldBe termApply.structure
  }
}
