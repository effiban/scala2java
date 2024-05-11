package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.CoreTermSelectTransformer.transform

import scala.meta.{Term, XtensionQuasiquoteTerm}

class CoreTermSelectTransformerTest extends UnitTestSuite {

  test("transform 'scala.Nil' should return 'java.util.List.of()'") {
    transform(ScalaNil).value.structure shouldBe q"java.util.List.of()".structure
  }

  test("transform 'scala.None' should return 'java.util.Optional.empty()'") {
    transform(ScalaNone).value.structure shouldBe q"java.util.Optional.empty()".structure
  }

  test("transform 'Dummy.dummy' should return None") {
    val termSelect = Term.Select(Term.Name("Dummy"), Term.Name("dummy"))

    transform(termSelect) shouldBe None
  }
}
