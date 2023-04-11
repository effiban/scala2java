package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.CoreTermSelectTransformer.transform

import scala.meta.Term

class CoreTermSelectTransformerTest extends UnitTestSuite {

  test("transform 'myTuple._1' should return 'myTuple.v1'") {
    val scalaTermSelect = Term.Select(Term.Name("myTuple"), Term.Name("_1"))
    val expectedJavaTermSelect = Term.Select(Term.Name("myTuple"), Term.Name("v1"))

    transform(scalaTermSelect).value.structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform 'Dummy.dummy' should return None") {
    val termSelect = Term.Select(Term.Name("Dummy"), Term.Name("dummy"))

    transform(termSelect) shouldBe None
  }
}
