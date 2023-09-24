package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TermSelectImporterMatcher.findMatch
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Importer, Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class TermSelectImporterMatcherTest extends UnitTestSuite {

  private val PositiveScenarios = Table(
    ("Term.Select", "Importer", "ExpectedMatchingImporter"),
    (q"a.b.C", importer"a.b.C", importer"a.b.C"),
    (q"a.b.C", importer"a.b._", importer"a.b._"),
    (q"a.b.C", importer"a.b.{C, D}", importer"a.b.C"),
  )

  private val NegativeScenarios = Table(
    ("Term.Select", "Importer"),
    (q"a.b.C", importer"e.f.G"),
    (q"a.b.D", importer"a.c.D"),
    (q"a.b.C", importer"a.b"),
    (q"b.C", importer"a.b.C")
  )

  forAll(PositiveScenarios) { (termSelect: Term.Select, importer: Importer, expectedMatchingImporter: Importer) =>
    test(s"Type '$termSelect' should match importer '$importer'") {
      findMatch(termSelect, importer).value.structure shouldBe expectedMatchingImporter.structure
    }
  }

  forAll(NegativeScenarios) { (termSelect: Term.Select, importer: Importer) =>
    test(s"Type '$termSelect' should not match importer '$importer'") {
      findMatch(termSelect, importer) shouldBe None
    }
  }
}
