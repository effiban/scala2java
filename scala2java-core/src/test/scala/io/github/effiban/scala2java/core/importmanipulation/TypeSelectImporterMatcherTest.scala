package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterMatcher.findMatch
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeSelectImporterMatcherTest extends UnitTestSuite {

  private val PositiveScenarios = Table(
    ("Type.Select", "Importer", "ExpectedMatchingImporter"),
    (t"a.b.C", importer"a.b.C", importer"a.b.C"),
    (t"a.b.C", importer"a.b._", importer"a.b._"),
    (t"a.b.C", importer"a.b.{C, D}", importer"a.b.C"),
  )

  private val NegativeScenarios = Table(
    ("Type.Select", "Importer"),
    (t"a.b.C", importer"e.f.G"),
    (t"a.b.D", importer"a.c.D"),
    (t"a.b.C", importer"a.b"),
    (t"b.C", importer"a.b.C")
  )

  forAll(PositiveScenarios) { (typeSelect: Type.Select, importer: Importer, expectedMatchingImporter: Importer) =>
    test(s"Type '$typeSelect' should match importer '$importer'") {
      findMatch(typeSelect, importer).value.structure shouldBe expectedMatchingImporter.structure
    }
  }

  forAll(NegativeScenarios) { (typeSelect: Type.Select, importer: Importer) =>
    test(s"Type '$typeSelect' should not match importer '$importer'") {
      findMatch(typeSelect, importer) shouldBe None
    }
  }
}
