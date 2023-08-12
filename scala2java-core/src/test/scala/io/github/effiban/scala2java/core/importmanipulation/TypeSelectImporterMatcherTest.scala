package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterMatcher.matches
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeSelectImporterMatcherTest extends UnitTestSuite {

  private val MatcherScenarios = Table(
    ("Type.Select", "Importer", "ExpectedResult"),
    (t"a.b.C", importer"a.b.C", true),
    (t"a.b.C", importer"a.b._", true),
    (t"a.b.C", importer"e.f.G", false),
    (t"a.b.D", importer"a.c.D", false),
    (t"a.b.C", importer"a.b", false),
    (t"b.C", importer"a.b.C", false)
  )

  forAll(MatcherScenarios) { (typeSelect: Type.Select, importer: Importer, expectedResult: Boolean) =>
    test(s"Type '$typeSelect' should ${if (!expectedResult) "not " else ""}match importer '$importer'") {
      matches(typeSelect, importer) shouldBe expectedResult
    }
  }
}
