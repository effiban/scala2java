package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterMatcher
import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeSelectUnqualifierImplTest extends UnitTestSuite {

  private val typeSelectImporterMatcher = mock[TypeSelectImporterMatcher]

  private val typeSelectUnqualifier = new TypeSelectUnqualifierImpl(typeSelectImporterMatcher)

  test("unqualify() for scala.Array should return unchanged") {
    typeSelectUnqualifier.unqualify(TypeSelects.ScalaArray).structure shouldBe TypeSelects.ScalaArray.structure
  }

  test("unqualify() for scala.Enumeration should return unchanged") {
    typeSelectUnqualifier.unqualify(TypeSelects.ScalaEnumeration).structure shouldBe TypeSelects.ScalaEnumeration.structure
  }

  test("unqualify() for non-Array/Enumeration when no importers provided should return unchanged") {
    val typeSelect = t"a.b.C"
    typeSelectUnqualifier.unqualify(typeSelect).structure shouldBe typeSelect.structure
  }

  test("unqualify() for non-Array/Enumeration when one importer provided and doesn't match, should return unchanged") {
    val typeSelect = t"a.b.C"
    val importer = importer"c.d.E"

    doReturn(None).when(typeSelectImporterMatcher).findMatch(eqTree(typeSelect), eqTree(importer))

    typeSelectUnqualifier.unqualify(typeSelect, QualificationContext(List(importer))).structure shouldBe typeSelect.structure
  }

  test("unqualify() for non-Array/Enumeration when one importer provided and matches, should return name only") {
    val typeSelect = t"a.b.C"
    val importer = importer"a.b.{C, D}"
    val expectedMatchingImporter = importer"a.b.C"

    doReturn(Some(expectedMatchingImporter)).when(typeSelectImporterMatcher).findMatch(eqTree(typeSelect), eqTree(importer))

    typeSelectUnqualifier.unqualify(typeSelect, QualificationContext(List(importer))).structure shouldBe t"C".structure
  }

  test("unqualify() for non-Array/Enumeration when two importers provided and none match, should return unchanged") {
    val typeSelect = t"a.b.C"
    val importer1 = importer"d.e.F"
    val importer2 = importer"g.h.I"

    doReturn(None).when(typeSelectImporterMatcher).findMatch(eqTree(typeSelect), any[Importer])

    typeSelectUnqualifier.unqualify(typeSelect, QualificationContext(List(importer1, importer2))).structure shouldBe typeSelect.structure
  }

  test("unqualify() for non-Array/Enumeration when two importers provided and second one matches, should return name only") {
    val typeSelect = t"a.b.C"
    val importer1 = importer"d.e.F"
    val importer2 = importer"a.b.{C, D}"
    val expectedMatchingImporter = importer"a.b.C"

    doAnswer((_: Type.Select, importer: Importer) => importer match {
      case anImporter if anImporter.structure == importer2.structure => Some(expectedMatchingImporter)
      case _ => None
    }).when(typeSelectImporterMatcher).findMatch(eqTree(typeSelect), any[Importer])

    typeSelectUnqualifier.unqualify(typeSelect, QualificationContext(List(importer1, importer2))).structure shouldBe t"C".structure
  }
}
