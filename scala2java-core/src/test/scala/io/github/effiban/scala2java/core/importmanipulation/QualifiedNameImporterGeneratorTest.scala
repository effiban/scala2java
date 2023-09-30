package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.QualifiedNameImporterGenerator.{generateForStaticField, generateForStaticMethod, generateForType}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class QualifiedNameImporterGeneratorTest extends UnitTestSuite {

  test("generateForType() when valid should return a corresponding importer") {
    generateForType(q"java.util", "Optional").value.structure shouldBe importer"java.util.Optional".structure
  }

  test("generateForType() when invalid should return None") {
    generateForType(q"aaa.bbb", "CC") shouldBe None
  }

  test("generateForStaticMethod() when valid should return a corresponding importer") {
    generateForStaticMethod(q"java.util.List", "of", List(q"3", q"4")).value.structure shouldBe importer"java.util.List.of".structure
  }

  test("generateForStaticMethod() when method is not static should return None") {
    generateForStaticMethod(q"java.util.ArrayList", "size", Nil) shouldBe None
  }

  test("generateForStaticMethod() when method is not public should return None") {
    generateForStaticMethod(q"java.util.ArrayList", "elementAt", Nil) shouldBe None
  }

  test("generateForStaticMethod() when class exists but method doesn't should return None") {
    generateForStaticMethod(q"java.util.ArrayList", "bla", Nil) shouldBe None
  }

  test("generateForStaticMethod() when class doesn't exist should return None") {
    generateForStaticMethod(q"Bla", "bla", Nil) shouldBe None
  }

  test("generateForStaticField() when valid should return a corresponding importer") {
    generateForStaticField(q"System", "out").value.structure shouldBe importer"System.out".structure
  }

  test("generateForStaticField() when field is not public should return None") {
    generateForStaticField(q"java.util.ArrayList", "size") shouldBe None
  }

  test("generateForStaticField() when class exists but field doesn't should return None") {
    generateForStaticField(q"System", "bla") shouldBe None
  }

  test("generateForStaticField() when class doesn't exist should return None") {
    generateForStaticField(q"Bla", "bla") shouldBe None
  }
}
