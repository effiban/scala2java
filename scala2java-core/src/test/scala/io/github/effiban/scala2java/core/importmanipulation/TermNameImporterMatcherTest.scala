package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.importmanipulation.TermNameImporterMatcher.findMatch
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Importer, Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class TermNameImporterMatcherTest extends UnitTestSuite {
  private val PositiveScenarios = Table(
    ("Term.Name", "Importer", "ExpectedMatchingImporter"),
    (q"C", importer"a.b.C", importer"a.b.C"),
    (q"C", importer"a.b.{C, D}", importer"a.b.C"),
  )

  private val NegativeScenarios = Table(
    ("Term.Name", "Importer"),
    (q"C", importer"a.b._"),
    (q"b", importer"a.b._"),
    (q"C", importer"a.b.{D, E}"),
    (q"C", importer"a.b"),
    (q"C", importer"a.C.D")
  )

  forAll(PositiveScenarios) { (termName: Term.Name, importer: Importer, expectedMatchingImporter: Importer) =>
    test(s"Term '$termName' should match importer '$importer'") {
      findMatch(termName, importer).value.structure shouldBe expectedMatchingImporter.structure
    }
  }

  forAll(NegativeScenarios) { (typeName: Term.Name, importer: Importer) =>
    test(s"Term '$typeName' should not match importer '$importer'") {
      findMatch(typeName, importer) shouldBe None
    }
  }

}
