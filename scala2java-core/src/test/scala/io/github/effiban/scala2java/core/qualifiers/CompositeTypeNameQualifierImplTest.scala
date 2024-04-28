package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.TypeNameImporterMatcher
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class CompositeTypeNameQualifierImplTest extends UnitTestSuite {

  private val typeNameImporterMatcher = mock[TypeNameImporterMatcher]
  private val coreTypeNameQualifier = mock[CoreTypeNameQualifier]

  private val compositeTypeNameQualifier = new CompositeTypeNameQualifierImpl(typeNameImporterMatcher, coreTypeNameQualifier)

  test("qualify when match found by importer should return qualified type") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val typeName = t"F"
    val expectedMatchingImporter = importer"d.F"
    val expectedQualifiedType = t"d.F"

    doAnswer((_: Type.Name, importer: Importer) => importer match {
      case anImporter if anImporter.structure == importer2.structure => Some(expectedMatchingImporter)
      case _ => None
    }).when(typeNameImporterMatcher).findMatch(eqTree(typeName), any[Importer])

    compositeTypeNameQualifier.qualify(typeName, QualificationContext(importers)).structure shouldBe expectedQualifiedType.structure
  }

  test("qualify when has importers and match found by core qualifier should return qualified type") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val typeName = t"Int"
    val expectedQualifiedType = t"scala.Int"

    doReturn(None).when(typeNameImporterMatcher).findMatch(eqTree(typeName), any[Importer])
    doReturn(Some(expectedQualifiedType)).when(coreTypeNameQualifier).qualify(eqTree(typeName))

    compositeTypeNameQualifier.qualify(typeName, QualificationContext(importers)).structure shouldBe expectedQualifiedType.structure
  }

  test("qualify when has importers and no match found by import or by core qualifier - should return unchanged") {
    val importer1 = importer"a.{B, C}"
    val importer2 = importer"d.{E, F}"
    val importers = List(importer1, importer2)

    val typeName = t"Foo"

    doReturn(None).when(typeNameImporterMatcher).findMatch(eqTree(typeName), any[Importer])
    doReturn(None).when(coreTypeNameQualifier).qualify(eqTree(typeName))

    compositeTypeNameQualifier.qualify(typeName, QualificationContext(importers)).structure shouldBe typeName.structure
  }

  test("qualify when has no importers and match found by core qualifier should return qualified type") {
    val typeName = t"Int"
    val expectedQualifiedType = t"scala.Int"

    doReturn(Some(expectedQualifiedType)).when(coreTypeNameQualifier).qualify(eqTree(typeName))

    compositeTypeNameQualifier.qualify(typeName, QualificationContext()).structure shouldBe expectedQualifiedType.structure
  }

  test("qualify when has no importers and no match found by core qualifier should return unchanged") {
    val typeName = t"Foo"

    doReturn(None).when(coreTypeNameQualifier).qualify(eqTree(typeName))

    compositeTypeNameQualifier.qualify(typeName, QualificationContext()).structure shouldBe typeName.structure
  }

  test("qualify when Type.Name has a parent Class should ignore matching importers") {
    val aClass =
      q"""
      class A {
      }
      """

    val importers = List(importer"a.A")

    compositeTypeNameQualifier.qualify(aClass.name, QualificationContext(importers)).structure shouldBe aClass.name.structure
  }

  test("qualify when Type.Name has a parent Class should not invoke core qualifier") {
    val aClass =
      q"""
      class A {
      }
      """

    compositeTypeNameQualifier.qualify(aClass.name, QualificationContext())

    verifyNoInteractions(coreTypeNameQualifier)
  }

  test("qualify when Type.Name has a parent Trait should ignore matching importers") {
    val aTrait =
      q"""
      trait A {
      }
      """

    val importers = List(importer"a.A")

    compositeTypeNameQualifier.qualify(aTrait.name, QualificationContext(importers)).structure shouldBe aTrait.name.structure
  }

  test("qualify when Type.Name has a parent Trait should not invoke core qualifier") {
    val aTrait =
      q"""
      trait A {
      }
      """

    compositeTypeNameQualifier.qualify(aTrait.name, QualificationContext())

    verifyNoInteractions(coreTypeNameQualifier)
  }

  test("qualify when Type.Name has a parent Type.Param should ignore matching importers") {
    val typeParam = tparam"T"

    val importers = List(importer"t.T")

    compositeTypeNameQualifier.qualify(typeParam.name.asInstanceOf[Type.Name], QualificationContext(importers)).structure shouldBe
      typeParam.name.structure
  }

  test("qualify when Type.Name has a parent Type.Param should not invoke core qualifier") {
    val typeParam = tparam"T"

    compositeTypeNameQualifier.qualify(typeParam.name.asInstanceOf[Type.Name], QualificationContext())

    verifyNoInteractions(coreTypeNameQualifier)
  }
}
