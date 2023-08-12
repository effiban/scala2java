package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeSelectImporterMatcher
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeSelectUnqualifierImplTest extends UnitTestSuite {

  private val typeSelectImporterMatcher = mock[TypeSelectImporterMatcher]

  private val typeSelectUnqualifier = new TypeSelectUnqualifierImpl(typeSelectImporterMatcher)

  test("unqualify() when no importers provided should return unchanged") {
    val typeSelect = t"a.b.C"
    typeSelectUnqualifier.unqualify(typeSelect, Nil).structure shouldBe typeSelect.structure
  }

  test("unqualify() when one importer provided and doesn't match, should return unchanged") {
    val typeSelect = t"a.b.C"
    val importer = importer"c.d.E"

    doReturn(false).when(typeSelectImporterMatcher).matches(eqTree(typeSelect), eqTree(importer))

    typeSelectUnqualifier.unqualify(typeSelect, List(importer)).structure shouldBe typeSelect.structure
  }

  test("unqualify() when one importer provided and matches, should return name only") {
    val typeSelect = t"a.b.C"
    val importer = importer"a.b.C"

    doReturn(true).when(typeSelectImporterMatcher).matches(eqTree(typeSelect), eqTree(importer))

    typeSelectUnqualifier.unqualify(typeSelect, List(importer)).structure shouldBe t"C".structure
  }

  test("unqualify() when two importers provided and none match, should return unchanged") {
    val typeSelect = t"a.b.C"
    val importer1 = importer"d.e.F"
    val importer2 = importer"g.h.I"

    doReturn(false).when(typeSelectImporterMatcher).matches(eqTree(typeSelect), any[Importer])

    typeSelectUnqualifier.unqualify(typeSelect, List(importer1, importer2)).structure shouldBe typeSelect.structure
  }

  test("unqualify() when two importers provided and second one matches, should return name only") {
    val typeSelect = t"a.b.C"
    val importer1 = importer"d.e.F"
    val importer2 = importer"a.b.C"

    doAnswer((_: Type.Select, importer: Importer) => importer match {
      case anImporter if anImporter.structure == importer2.structure => true
      case _ => false
    }).when(typeSelectImporterMatcher).matches(eqTree(typeSelect), any[Importer])

    typeSelectUnqualifier.unqualify(typeSelect, List(importer1, importer2)).structure shouldBe t"C".structure
  }
}
