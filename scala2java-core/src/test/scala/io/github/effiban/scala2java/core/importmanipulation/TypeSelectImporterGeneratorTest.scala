package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterGenerator.generate
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeSelectImporterGeneratorTest extends UnitTestSuite {

  test("generate for a non-Array should return the corresponding Importer") {
    generate(t"a.b.C").value.structure shouldBe importer"a.b.C".structure
  }

  test("generate for an untyped Array should return None") {
    generate(t"scala.Array") shouldBe None
  }
}
