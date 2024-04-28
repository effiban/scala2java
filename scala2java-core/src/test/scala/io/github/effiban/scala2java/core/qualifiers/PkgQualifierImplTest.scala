package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TermSelects.{ScalaNil, ScalaNone}
import io.github.effiban.scala2java.core.entities.TypeSelects.{ScalaDouble, ScalaInt}
import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
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

  test("qualify when has nested Term.Names but TermNameQualifier returns unchanged, should return unchanged") {
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
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when has nested Term.Names and TermNameQualifier qualifies some of them, should return those terms qualified") {
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
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has nested Term.Selects but for the first term of all, TermNameQualifier returns unchanged - should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object A {
          val x = CC.DD
        }
        object B {
          val y = EE.FF
        }
      }
      """


    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object A {
          val x = CC.DD
      }
      """,
      q"""
      object B {
          val y = EE.FF
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when has a nested Term.Select 'g.h' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = g.h
          val y = ZZ.WW
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        val x = g.h
        val y = ZZ.WW
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = qual.g.h
          val y = ZZ.WW
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has a nested Term.Select 'g.h.i' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = g.h.i
          val y = ZZ.WW
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        val x = g.h.i
        val y = ZZ.WW
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = qual.g.h.i
          val y = ZZ.WW
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has a nested Term.Select 'g.h' should NOT try to qualify 'h'") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = g.h
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        val x = g.h
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg)

    verify(termNameQualifier, never).qualify(eqTree(q"h"), eqQualificationContext(QualificationContext(expectedImporters)))
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
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when TypeNameQualifier qualifies some of the nested types, should return those types qualified") {
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

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      trait C {
        val x: Int = 2
        val y: Double = 3.3
      }
      """
    )

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

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName).when(termNameQualifier).qualify(any[Term.Name],
      eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"Int".structure => ScalaInt
      case aTypeName if aTypeName.structure == t"Double".structure => ScalaDouble
      case aTypeName => aTypeName
    }).when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has nested Type.Selects but for the first term of all, TermNameQualifier returns nothing - should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object A {
          var x: CC.DD
        }
        object B {
          var y: EE.FF
        }
      }
      """


    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object A {
          var x: CC.DD
      }
      """,
      q"""
      object B {
          var y: EE.FF
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when has a nested Type.Select 'g.H' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: g.H
          val y: ZZ.WW
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        var x: g.H
        var y: ZZ.WW
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: qual.g.H
          var y: ZZ.WW
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has a nested Type.Select 'g.h.I' and TermNameQualifier qualifies 'g' should return it qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: g.h.I
          val y: ZZ.WW
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        var x: g.H.I
        var y: ZZ.WW
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: qual.g.H.I
          var y: ZZ.WW
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has a nested Type.Select 'g.H' should NOT try to qualify 'H'") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: g.H
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        var x: g.H
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg)

    verify(termNameQualifier, never).qualify(eqTree(q"H"), eqQualificationContext(QualificationContext(expectedImporters)))
  }

  test("qualify when has nested Type.Projects but for the first term of all, TypeNameQualifier returns nothing - should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object A {
          var x: CC#DD
        }
        object B {
          var y: EE#FF
        }
      }
      """


    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object A {
          var x: CC#DD
      }
      """,
      q"""
      object B {
          var y: EE#FF
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when has a nested Type.Project 'G#H' and TypeNameQualifier qualifies 'G' should return it qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: G#H
          val y: ZZ#WW
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        var x: G#H
        var y: ZZ#WW
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: qual.G#H
          var y: ZZ#WW
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"G".structure => t"qual.G"
      case aTypeName => aTypeName
    }).when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has a nested Type.Project 'g.H#I' and TypeNameQualifier qualifies 'g' should return it qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: g.H#I
          val y: ZZ#WW
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        var x: g.H#I
        var y: ZZ#WW
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: qual.g.H#I
          var y: ZZ#WW
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName match {
      case aTermName if aTermName.structure == q"g".structure => q"qual.g"
      case aTermName => aTermName
    }).when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has a nested Type.Project 'G#H#I' and TypeNameQualifier qualifies 'G' should return it qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: G#H#I
          val y: ZZ#WW
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        var x: G#H#I
        var y: ZZ#WW
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: qual.G#H#I
          var y: ZZ#WW
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName match {
      case aTypeName if aTypeName.structure == t"G".structure => t"qual.G"
      case aTypeName => aTypeName
    }).when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has a nested Type.Project 'G#H' should NOT try to qualify 'H'") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          var x: G#H
        }
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        var x: G#H
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termName: Term.Name) => termName)
      .when(termNameQualifier).qualify(any[Term.Name], eqQualificationContext(QualificationContext(expectedImporters)))
    doAnswer((typeName: Type.Name) => typeName)
      .when(typeNameQualifier).qualify(any[Type.Name], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg)

    verify(typeNameQualifier, never).qualify(eqTree(t"H"), eqQualificationContext(QualificationContext(expectedImporters)))
  }
}
