package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaDouble, ScalaInt}
import io.github.effiban.scala2java.core.importmanipulation.ImporterCollector
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgQualifierImplTest extends UnitTestSuite {

  private val importerCollector = mock[ImporterCollector]
  private val typeNameQualifier = mock[CompositeTypeNameQualifier]

  private val pkgQualifier = new PkgQualifierImpl(importerCollector, typeNameQualifier)

  test("qualify when inner qualifier returns unchanged for all, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.d
        import e.f

        trait G {
        }
        trait H {
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")

    doReturn(expectedImporters).when(importerCollector).collectFlat(eqTreeList(pkg.stats))
    doAnswer((typeName: Type.Name) => typeName).when(typeNameQualifier).qualify(any[Type.Name], eqTreeList(expectedImporters))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when inner qualifier qualifies some of the types, should return those types qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        trait C {
          val x: Int = 2
          val y: Double = 3.3
        }
      }
      """

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        trait C {
          val x: scala.Int = 2
          val y: scala.Double = 3.3
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")

    doReturn(expectedImporters).when(importerCollector).collectFlat(eqTreeList(initialPkg.stats))
    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"Int".structure => ScalaInt
      case aTypeName if aTypeName.structure == t"Double".structure => ScalaDouble
      case aTypeName => aTypeName
    }).when(typeNameQualifier).qualify(any[Type.Name], eqTreeList(expectedImporters))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
