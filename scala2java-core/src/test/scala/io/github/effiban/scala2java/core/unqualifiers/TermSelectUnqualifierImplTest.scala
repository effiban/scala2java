package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TermSelectImporterMatcher
import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class TermSelectUnqualifierImplTest extends UnitTestSuite {

  private val termSelectImporterMatcher = mock[TermSelectImporterMatcher]

  private val termSelectUnqualifier = new TermSelectUnqualifierImpl(termSelectImporterMatcher)

  test("unqualify() when no importers provided should return unchanged") {
    val typeSelect = q"a.b.C"
    termSelectUnqualifier.unqualify(typeSelect).structure shouldBe typeSelect.structure
  }

  test("unqualify() when one importer provided and doesn't match, should return unchanged") {
    val typeSelect = q"a.b.C"
    val importer = importer"c.d.E"

    doReturn(None).when(termSelectImporterMatcher).findMatch(eqTree(typeSelect), eqTree(importer))

    termSelectUnqualifier.unqualify(typeSelect, QualificationContext(List(importer))).structure shouldBe typeSelect.structure
  }

  test("unqualify() when one importer provided and matches, should return name only") {
    val typeSelect = q"a.b.C"
    val importer = importer"a.b.{C, D}"
    val expectedMatchingImporter = importer"a.b.C"

    doReturn(Some(expectedMatchingImporter)).when(termSelectImporterMatcher).findMatch(eqTree(typeSelect), eqTree(importer))

    termSelectUnqualifier.unqualify(typeSelect, QualificationContext(List(importer))).structure shouldBe q"C".structure
  }

  test("unqualify() when two importers provided and none match, should return unchanged") {
    val typeSelect = q"a.b.C"
    val importer1 = importer"d.e.F"
    val importer2 = importer"g.h.I"

    doReturn(None).when(termSelectImporterMatcher).findMatch(eqTree(typeSelect), any[Importer])

    termSelectUnqualifier.unqualify(typeSelect, QualificationContext(List(importer1, importer2))).structure shouldBe typeSelect.structure
  }

  test("unqualify() when two importers provided and second one matches, should return name only") {
    val typeSelect = q"a.b.C"
    val importer1 = importer"d.e.F"
    val importer2 = importer"a.b.{C, D}"
    val expectedMatchingImporter = importer"a.b.C"

    doAnswer((_: Term.Select, importer: Importer) => importer match {
      case anImporter if anImporter.structure == importer2.structure => Some(expectedMatchingImporter)
      case _ => None
    }).when(termSelectImporterMatcher).findMatch(eqTree(typeSelect), any[Importer])

    termSelectUnqualifier.unqualify(typeSelect, QualificationContext(List(importer1, importer2))).structure shouldBe q"C".structure
  }

}
