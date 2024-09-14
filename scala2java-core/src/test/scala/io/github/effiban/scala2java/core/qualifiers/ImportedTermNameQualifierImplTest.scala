package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermNameImporterMatcher
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class ImportedTermNameQualifierImplTest extends UnitTestSuite {
    private val termNameImporterMatcher = mock[TermNameImporterMatcher]

    private val importedTermNameQualifier = new ImportedTermNameQualifierImpl(termNameImporterMatcher)

    test("qualify when match found by importer should return qualified term") {
      val importer1 = importer"a.{B, C}"
      val importer2 = importer"d.{E, F}"
      val importers = List(importer1, importer2)

      val termName = q"F"
      val expectedMatchingImporter = importer"d.F"
      val expectedQualifiedTerm = q"d.F"

      doAnswer((_: Term.Name, importer: Importer) => importer match {
        case anImporter if anImporter.structure == importer2.structure => Some(expectedMatchingImporter)
        case _ => None
      }).when(termNameImporterMatcher).findMatch(eqTree(termName), any[Importer])

      importedTermNameQualifier.qualify(termName, importers).value.structure shouldBe expectedQualifiedTerm.structure
    }

    test("qualify when has importers and no match found - should return None") {
      val importer1 = importer"a.{B, C}"
      val importer2 = importer"d.{E, F}"
      val importers = List(importer1, importer2)

      val termName = q"Foo"

      doReturn(None).when(termNameImporterMatcher).findMatch(eqTree(termName), any[Importer])

      importedTermNameQualifier.qualify(termName, importers) shouldBe None
    }

    test("qualify when has no importers should return None") {
      val termName = q"Foo"

      importedTermNameQualifier.qualify(termName) shouldBe None
    }
}
