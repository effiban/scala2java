package io.github.effiban.scala2java.predicates

import io.github.effiban.scala2java.classifiers.ImporterClassifier
import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.testsuites.UnitTestSuite
import io.github.effiban.scala2java.testtrees.TermNames.Scala

import scala.meta.{Importee, Importer, Name, Term}

class ImporterIncludedPredicateImplTest extends UnitTestSuite {

  private val importerClassifier = mock[ImporterClassifier]

  private val importerIncludedPredicate = new ImporterIncludedPredicateImpl(importerClassifier)

  test("apply() when it is not a scala importer") {
    val importer = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )

    when(importerClassifier.isScala(eqTree(importer))).thenReturn(false)

    importerIncludedPredicate(importer) shouldBe true
  }

  test("apply() when it is a scala importer") {
    val scalaImporter = Importer(
      ref = Scala,
      importees = List(Importee.Name(Name.Indeterminate("someName")))
    )

    when(importerClassifier.isScala(eqTree(scalaImporter))).thenReturn(true)

    importerIncludedPredicate(scalaImporter) shouldBe false
  }

}
