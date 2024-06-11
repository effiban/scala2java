package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter
import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Importer, Stat, Term, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class PkgUnqualifierImplTest extends UnitTestSuite {

  private val statsByImportSplitter = mock[StatsByImportSplitter]
  private val treeUnqualifier = mock[TreeUnqualifier]

  private val pkgUnqualifier = new PkgUnqualifierImpl(
    statsByImportSplitter,
    treeUnqualifier
  )

  test("unqualify when has no nested qualified trees, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.C
        import d.D
      }
      """

    val expectedImporters = List(importer"c.C", importer"d.D")

    doReturn((expectedImporters, Nil)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))

    pkgUnqualifier.unqualify(pkg).structure shouldBe pkg.structure

    verifyNoInteractions(treeUnqualifier)
  }

  test("unqualify when has nested qualified trees but TreeQualifier returns unchanged for all, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.C
        import d.D

        trait MyTrait {
          def foo(): Unit = {
            x.doSomething()
          }
        }
      }
      """

    val stat =
      q"""
      trait MyTrait {
        def foo(): Unit = {
          x.doSomething()
        }
      }
      """

    val expectedImporters = List(importer"c.C", importer"d.D")
    val expectedNonImports = List(stat)

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((stat: Stat) => stat)
      .when(treeUnqualifier).unqualify(any[Stat], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(pkg).structure shouldBe pkg.structure
  }

  test("unqualify when TreeQualifier unqualifies some of the trees, should return them unqualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.c1
        import d.d1

        trait Trait1 {
          val x = c.c1()
        }
        trait Trait2 {
          val y = d.d1()
        }
      }
      """

    val initialStat1 =
      q"""
      trait Trait1 {
        val x = c.c1()
      }
      """

    val expectedImporters = List(importer"c.c1", importer"d.d1")
    val expectedNonImports = List(
      initialStat1,

      q"""
      trait Trait2 {
        val y = d.d1()
      }
      """
    )

    val expectedFinalStat1 =
      q"""
      trait Trait1 {
        val x = c1()
      }
      """

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.c1
        import d.d1

        trait Trait1 {
          val x = c1()
        }

        trait Trait2 {
          val y = d.d1()
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((stat: Stat, _: List[Importer]) => stat match {
      case aTermSelect if aTermSelect.structure == initialStat1.structure => expectedFinalStat1
      case aTermSelect => aTermSelect
    }).when(treeUnqualifier).unqualify(any[Term.Select], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("unqualify when TreeQualifier unqualifies all of the trees, should return them unqualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.c1
        import d.d1

        trait Trait1 {
          val x = c.c1()
        }
        trait Trait2 {
          val y = d.d1()
        }
      }
      """

    val initialStat1 =
      q"""
      trait Trait1 {
        val x = c.c1()
      }
      """

    val initialStat2 =
      q"""
      trait Trait2 {
        val y = d.d1()
      }
      """

    val expectedImporters = List(importer"c.c1", importer"d.d1")
    val expectedNonImports = List(initialStat1, initialStat2)

    val expectedFinalStat1 =
      q"""
      trait Trait1 {
        val x = c1()
      }
      """

    val expectedFinalStat2 =
      q"""
      trait Trait2 {
        val y = d1()
      }
      """


    val expectedFinalPkg =
      q"""
      package a.b {
        import c.c1
        import d.d1

        trait Trait1 {
          val x = c1()
        }
        trait Trait2 {
          val y = d1()
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((stat: Stat, _: List[Importer]) => stat match {
      case aStat if aStat.structure == initialStat1.structure => expectedFinalStat1
      case aStat if aStat.structure == initialStat2.structure => expectedFinalStat2
      case aStat => aStat
    }).when(treeUnqualifier).unqualify(any[Stat], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
