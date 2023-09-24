package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermNameImporterMatcher
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class CompositeTermNameQualifierImplTest extends UnitTestSuite {
  private val termNameImporterMatcher = mock[TermNameImporterMatcher]
  private val coreTermNameQualifier = mock[CoreTermNameQualifier]

  private val compositeTermNameQualifier = new CompositeTermNameQualifierImpl(termNameImporterMatcher, coreTermNameQualifier)

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

    compositeTermNameQualifier.qualify(termName, importers).structure shouldBe expectedQualifiedTerm.structure
  }

  test("qualify when has importers and match found by core qualifier should return qualified term") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val termName = q"Int"
    val expectedQualifiedTerm = q"scala.Int"

    doReturn(None).when(termNameImporterMatcher).findMatch(eqTree(termName), any[Importer])
    doReturn(Some(expectedQualifiedTerm)).when(coreTermNameQualifier).qualify(eqTree(termName))

    compositeTermNameQualifier.qualify(termName, importers).structure shouldBe expectedQualifiedTerm.structure
  }

  test("qualify when has importers and no match found by import or by core qualifier - should return unchanged") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val termName = q"Foo"

    doReturn(None).when(termNameImporterMatcher).findMatch(eqTree(termName), any[Importer])
    doReturn(None).when(coreTermNameQualifier).qualify(eqTree(termName))

    compositeTermNameQualifier.qualify(termName, importers).structure shouldBe termName.structure
  }

  test("qualify when has no importers and match found by core qualifier should return qualified term") {
    val termName = q"Int"
    val expectedQualifiedTerm = q"scala.Int"

    doReturn(Some(expectedQualifiedTerm)).when(coreTermNameQualifier).qualify(eqTree(termName))

    compositeTermNameQualifier.qualify(termName, Nil).structure shouldBe expectedQualifiedTerm.structure
  }

  test("qualify when has no importers and no match found by core qualifier should return unchanged") {
    val termName = q"Foo"

    doReturn(None).when(coreTermNameQualifier).qualify(eqTree(termName))

    compositeTermNameQualifier.qualify(termName, Nil).structure shouldBe termName.structure
  }
}
