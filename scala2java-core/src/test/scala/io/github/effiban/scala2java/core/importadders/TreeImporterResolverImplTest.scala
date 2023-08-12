package io.github.effiban.scala2java.core.importadders

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeImporterResolverImplTest extends UnitTestSuite {

  private val typeSelectImporterResolver = mock[TypeSelectImporterResolver]

  private val treeImporterResolver = new TreeImporterResolverImpl(typeSelectImporterResolver)

  test("resolve for class with two members defined using Type.Selects should return Importers") {
    val typeSelect1 = t"a.b.C"
    val typeSelect2 = t"d.e.F"

    val importer1 = importer"a.b.C"
    val importer2 = importer"d.e.F"

    val theClass =
      q"""
      class MyClass {
        val x: a.b.C
        val y: d.e.F
      }
      """

    doAnswer((typeSelect: Type.Select) => typeSelect match {
      case aTypeSelect if aTypeSelect.structure == typeSelect1.structure => importer1
      case aTypeSelect if aTypeSelect.structure == typeSelect2.structure => importer2
      case aTypeSelect => throw new IllegalStateException(s"No stubbed importer answer defined for Type.Select $aTypeSelect")
    }).when(typeSelectImporterResolver).resolve(any[Type.Select])

    treeImporterResolver.resolve(theClass).structure shouldBe List(importer1, importer2).structure
  }

  test("resolve for class with two members defined using Type.Names should return empty") {
    val theClass =
      q"""
      class MyClass {
        val x: X
        val y: Y
      }
      """

    treeImporterResolver.resolve(theClass) shouldBe Nil
  }
}
