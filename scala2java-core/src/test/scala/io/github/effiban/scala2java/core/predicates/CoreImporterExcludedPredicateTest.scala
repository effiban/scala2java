package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.ImporterClassifier
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames.Scala
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Importee, Importer, Name, Term}

class CoreImporterExcludedPredicateTest extends UnitTestSuite {

  private val importerClassifier = mock[ImporterClassifier]

  private val importerExcludedPredicate = new CoreImporterExcludedPredicate(importerClassifier)

  test("apply() when it is not a scala importer") {
    val importer = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )

    when(importerClassifier.isScala(eqTree(importer))).thenReturn(false)

    importerExcludedPredicate(importer) shouldBe false
  }

  test("apply() when it is a scala importer") {
    val scalaImporter = Importer(
      ref = Scala,
      importees = List(Importee.Name(Name.Indeterminate("someName")))
    )

    when(importerClassifier.isScala(eqTree(scalaImporter))).thenReturn(true)

    importerExcludedPredicate(scalaImporter) shouldBe true
  }

}
