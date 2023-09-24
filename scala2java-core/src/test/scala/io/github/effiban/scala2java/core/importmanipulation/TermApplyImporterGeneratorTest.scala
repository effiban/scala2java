package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TermApplyImporterGenerator.generate
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class TermApplyImporterGeneratorTest extends UnitTestSuite {

  test("generate() for 'java.util.List.of()' should return the corresponding Importer") {
    generate(q"java.util.List.of()").value.structure shouldBe importer"java.util.List.of".structure
  }

  test("generate() for 'java.util.Optional.empty()' should return the corresponding Importer") {
    generate(q"java.util.Optional.empty()").value.structure shouldBe importer"java.util.Optional.empty".structure
  }

  test("generate() for 'a.foo()' should return None") {
    generate(q"a.foo()") shouldBe None
  }
}
