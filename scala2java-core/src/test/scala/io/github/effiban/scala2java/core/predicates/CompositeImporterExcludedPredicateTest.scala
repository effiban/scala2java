package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TermNames
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate
import org.mockito.ArgumentMatchers.any

import scala.meta.Importee.Wildcard
import scala.meta.Term.Select
import scala.meta.{Importer, Term}

class CompositeImporterExcludedPredicateTest extends UnitTestSuite {

  private val IncludedImporter1 = Importer(Select(TermNames.Java, Term.Name("io")), List(Wildcard()))
  private val IncludedImporter2 = Importer(Select(TermNames.Java, Term.Name("lang")), List(Wildcard()))
  private val ExcludedCoreImporter1 = Importer(Select(Term.Name("aaa"), Term.Name("b")), List(Wildcard()))
  private val ExcludedCoreImporter2 = Importer(Select(Term.Name("aaa"), Term.Name("c")), List(Wildcard()))
  private val ExcludedLibraryImporter1A = Importer(Select(Term.Name("bbb"), Term.Name("d")), List(Wildcard()))
  private val ExcludedLibraryImporter1B = Importer(Select(Term.Name("bbb"), Term.Name("e")), List(Wildcard()))
  private val ExcludedLibraryImporter2 = Importer(Select(Term.Name("ccc"), Term.Name("f")), List(Wildcard()))

  private val ImporterIncludedScenarios = Table(
    ("Desc", "Importer", "ExpectedResult"),
    ("IncludedImporter1", IncludedImporter1, true),
    ("IncludedImporter2", IncludedImporter2, true),
    ("ExcludedCoreImporter1", ExcludedCoreImporter1, false),
    ("ExcludedCoreImporter2", ExcludedCoreImporter2, false),
    ("ExcludedLibraryImporter1A", ExcludedLibraryImporter1A, false),
    ("ExcludedLibraryImporter1B", ExcludedLibraryImporter1B, false),
    ("ExcludedLibraryImporter2", ExcludedLibraryImporter2, false)
  )

  private val corePredicate = mock[ImporterExcludedPredicate]
  private val extensionPredicate1 = mock[ImporterExcludedPredicate]
  private val extensionPredicate2 = mock[ImporterExcludedPredicate]

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val compositePredicate = new CompositeImporterExcludedPredicate(corePredicate)

  override def beforeEach(): Unit = {
    when(extensionRegistry.importerExcludedPredicates).thenReturn(List(extensionPredicate1, extensionPredicate2))

    when(corePredicate.apply(any[Importer])).thenAnswer( (importer: Importer) => importer match {
      case anImporter if anImporter.structure == ExcludedCoreImporter1.structure => false
      case anImporter if anImporter.structure == ExcludedCoreImporter2.structure => false
      case _ => true
    })

    when(extensionPredicate1.apply(any[Importer])).thenAnswer((importer: Importer) => importer match {
      case anImporter if anImporter.structure == ExcludedLibraryImporter1A.structure => false
      case anImporter if anImporter.structure == ExcludedLibraryImporter1B.structure => false
      case _ => true
    })

    when(extensionPredicate2.apply(any[Importer])).thenAnswer((importer: Importer) => importer match {
      case anImporter if anImporter.structure == ExcludedLibraryImporter2.structure => false
      case _ => true
    })
  }

  forAll(ImporterIncludedScenarios) { (desc: String, importer: Importer, expectedResult: Boolean) =>
    test(s"""The importer '$desc' should be ${if (expectedResult) "included" else "excluded"}""") {
      compositePredicate.apply(importer) shouldBe expectedResult
    }
  }
}
