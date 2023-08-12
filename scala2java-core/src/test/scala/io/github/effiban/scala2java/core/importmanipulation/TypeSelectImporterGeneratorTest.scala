package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterGenerator.generate
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeSelectImporterGeneratorTest extends UnitTestSuite {

  test("generate") {
    generate(t"a.b.C").structure shouldBe importer"a.b.C".structure
  }
}
