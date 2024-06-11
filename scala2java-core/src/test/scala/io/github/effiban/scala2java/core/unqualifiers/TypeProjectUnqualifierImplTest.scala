package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeProjectImporterMatcher
import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteType}

class TypeProjectUnqualifierImplTest extends UnitTestSuite {

  private val typeProjectImporterMatcher = mock[TypeProjectImporterMatcher]

  private val typeProjectUnqualifier = new TypeProjectUnqualifierImpl(typeProjectImporterMatcher)

  test("unqualify() when no importers provided should return unchanged") {
    val typeProject = t"A#B"
    typeProjectUnqualifier.unqualify(typeProject).structure shouldBe typeProject.structure
  }

  test("unqualify() when one importer provided and doesn't match, should return unchanged") {
    val typeProject = t"A#B"
    val importer = importer"C.D"

    doReturn(None).when(typeProjectImporterMatcher).findMatch(eqTree(typeProject), eqTree(importer))

    typeProjectUnqualifier.unqualify(typeProject, QualificationContext(List(importer))).structure shouldBe typeProject.structure
  }

  test("unqualify() when one importer provided and matches, should return name only") {
    val typeProject = t"A#C"
    val importer = importer"A.{B, C}"
    val expectedMatchingImporter = importer"A.C"

    doReturn(Some(expectedMatchingImporter)).when(typeProjectImporterMatcher).findMatch(eqTree(typeProject), eqTree(importer))

    typeProjectUnqualifier.unqualify(typeProject, QualificationContext(List(importer))).structure shouldBe t"C".structure
  }

  test("unqualify() when two importers provided and none match, should return unchanged") {
    val typeProject = t"A#B#C"
    val importer1 = importer"D.E.F"
    val importer2 = importer"G.H.I"

    doReturn(None).when(typeProjectImporterMatcher).findMatch(eqTree(typeProject), any[Importer])

    typeProjectUnqualifier.unqualify(typeProject, QualificationContext(List(importer1, importer2))).structure shouldBe
      typeProject.structure
  }

  test("unqualify() when two importers provided and second one matches, should return name only") {
    val typeProject = t"A#B#C"
    val importer1 = importer"D.E.F"
    val importer2 = importer"A.B.{C, D}"
    val expectedMatchingImporter = importer"A.B.C"

    doAnswer((_: Type.Project, importer: Importer) => importer match {
      case anImporter if anImporter.structure == importer2.structure => Some(expectedMatchingImporter)
      case _ => None
    }).when(typeProjectImporterMatcher).findMatch(eqTree(typeProject), any[Importer])

    typeProjectUnqualifier.unqualify(typeProject, QualificationContext(List(importer1, importer2))).structure shouldBe t"C".structure
  }


}
