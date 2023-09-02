package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaArray, ScalaEnumeration}
import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterGenerator.generate
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeSelectImporterGeneratorTest extends UnitTestSuite {

  test("generate for a regular import should return the corresponding Importer") {
    generate(t"a.b.C").value.structure shouldBe importer"a.b.C".structure
  }

  test("generate for a Scala Array should return None") {
    generate(ScalaArray) shouldBe None
  }

  test("generate for a Scala Enumeration should return None") {
    generate(ScalaEnumeration) shouldBe None
  }
}
