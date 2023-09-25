package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaDouble, ScalaInt}
import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgQualifierImplTest extends UnitTestSuite {

  private val statsByImportSplitter = mock[StatsByImportSplitter]
  private val termNameQualifier = mock[CompositeTermNameQualifier]
  private val typeNameQualifier = mock[CompositeTypeNameQualifier]

  private val pkgQualifier = new PkgQualifierImpl(
    statsByImportSplitter,
    termNameQualifier,
    typeNameQualifier
  )

  test("qualify when has no nested trees that could be qualified, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.d
        import e.f
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")

    doReturn((expectedImporters, Nil)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when has nested Term.Names but TermNameQualifier returns nothing, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object A {
        }
        object B {
        }
      }
      """


    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object A {
      }
      """,
      q"""
      object B {
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((termName: Term.Name) => termName).when(termNameQualifier).qualify(any[Term.Name], eqTreeList(expectedImporters))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }


  test("qualify when has nested Type.Names but TypeNameQualifier returns nothing, should return unchanged") {
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
    val expectedNonImports = List(
      q"""
      trait G {
      }
      """,
      q"""
      trait H {
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((typeName: Type.Name) => typeName).when(typeNameQualifier).qualify(any[Type.Name], eqTreeList(expectedImporters))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when TermNameQualifier qualifies some of the terms, should return those terms qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = None
          val y = Nil
        }
      }
      """

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = scala.None
          val y = scala.Nil
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        val x = None
        val y = Nil
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"None".structure => ScalaNone
      case aTermName if aTermName.structure == q"Nil".structure => ScalaNil
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqTreeList(expectedImporters))
    doAnswer((typeName: Type.Name) => typeName).when(typeNameQualifier).qualify(any[Type.Name], eqTreeList(expectedImporters))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when TypeNameQualifier qualifies some of the types, should return those types qualified") {
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
    val expectedNonImports = List(
      q"""
      trait C {
        val x: Int = 2
        val y: Double = 3.3
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName).when(termNameQualifier).qualify(any[Term.Name], eqTreeList(expectedImporters))
    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"Int".structure => ScalaInt
      case aTypeName if aTypeName.structure == t"Double".structure => ScalaDouble
      case aTypeName => aTypeName
    }).when(typeNameQualifier).qualify(any[Type.Name], eqTreeList(expectedImporters))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
