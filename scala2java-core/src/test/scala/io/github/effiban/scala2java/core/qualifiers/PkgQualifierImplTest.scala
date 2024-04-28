package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Stat, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class PkgQualifierImplTest extends UnitTestSuite {

  private val statsByImportSplitter = mock[StatsByImportSplitter]
  private val statQualifier = mock[TreeQualifier]

  private val pkgQualifier = new PkgQualifierImpl(
    statsByImportSplitter,
    statQualifier
  )

  test("qualify when has no nested stats that could be qualified, should return unchanged") {
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

  test("qualify when has nested stats but StatQualifier returns unchanged, should return unchanged") {
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
    doAnswer((stat: Stat) => stat)
      .when(statQualifier).qualify(any[Stat], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(pkg).structure shouldBe pkg.structure
  }

  test("qualify when has two nested stats and StatQualifier qualifies one of them, should return that one qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = xx
        }
        object D {
          val y = yy
        }
      }
      """

    val initialStat2 =
      q"""
      object D {
        val y = yy
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        val x = xx
      }
      """,

      q"""
      object D {
        val y = yy
      }
      """
    )

    val expectedFinalStat2 =
      q"""
      object D {
        val y = qual.yy
      }
      """


    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = xx
        }
        object D {
          val y = qual.yy
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((stat: Stat) => stat match {
      case aStat if aStat.structure == initialStat2.structure => expectedFinalStat2
      case aStat => aStat
    }).when(statQualifier).qualify(any[Stat], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("qualify when has two nested stats and StatQualifier qualifies both, should return those stats qualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = xx
        }
        object D {
          val y = yy
        }
      }
      """

    val initialStat1 =
      q"""
      object C {
        val x = xx
      }
      """

    val initialStat2 =
      q"""
      object D {
        val y = yy
      }
      """

    val expectedImporters = List(importer"c.d", importer"e.f")
    val expectedNonImports = List(
      q"""
      object C {
        val x = xx
      }
      """,

      q"""
      object D {
        val y = yy
      }
      """
    )

    val expectedFinalStat1 =
      q"""
      object C {
        val x = qual.xx
      }
      """

    val expectedFinalStat2 =
      q"""
      object D {
        val y = qual.yy
      }
      """


    val expectedFinalPkg =
      q"""
      package a.b {
        import c.d
        import e.f

        object C {
          val x = qual.xx
        }
        object D {
          val y = qual.yy
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((stat: Stat) => stat match {
      case aStat if aStat.structure == initialStat1.structure => expectedFinalStat1
      case aStat if aStat.structure == initialStat2.structure => expectedFinalStat2
      case aStat => aStat
    }).when(statQualifier).qualify(any[Stat], eqQualificationContext(QualificationContext(expectedImporters)))

    pkgQualifier.qualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
