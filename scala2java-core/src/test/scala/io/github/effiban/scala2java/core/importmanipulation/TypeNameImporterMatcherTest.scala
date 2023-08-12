package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TypeNameImporterMatcher.findMatch
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeNameImporterMatcherTest extends UnitTestSuite {
  private val PositiveScenarios = Table(
    ("Type.Name", "Importer", "ExpectedMatchingImporter"),
    (t"C", importer"a.b.C", importer"a.b.C"),
    (t"C", importer"a.b.{C, D}", importer"a.b.C"),
  )

  private val NegativeScenarios = Table(
    ("Type.Name", "Importer"),
    (t"C", importer"a.b._"),
    (t"b", importer"a.b._"),
    (t"C", importer"a.b.{D, E}"),
    (t"C", importer"a.b"),
    (t"C", importer"a.C.D")
  )

  forAll(PositiveScenarios) { (typeName: Type.Name, importer: Importer, expectedMatchingImporter: Importer) =>
    test(s"Type '$typeName' should match importer '$importer'") {
      findMatch(typeName, importer).value.structure shouldBe expectedMatchingImporter.structure
    }
  }

  forAll(NegativeScenarios) { (typeName: Type.Name, importer: Importer) =>
    test(s"Type '$typeName' should not match importer '$importer'") {
      findMatch(typeName, importer) shouldBe None
    }
  }

}
