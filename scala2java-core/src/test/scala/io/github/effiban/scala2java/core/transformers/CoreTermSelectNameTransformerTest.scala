package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.CoreTermSelectNameTransformer.transform

import scala.meta.{Term, XtensionQuasiquoteTerm}

class CoreTermSelectNameTransformerTest extends UnitTestSuite {

  test("transform 'myTuple._1' should return 'myTuple.v1'") {
    transform(q"_1").structure shouldBe q"v1".structure
  }

  test("transform 'dummy' should return 'dummy'") {
    transform(q"dummy").structure shouldBe q"dummy".structure
  }
}
