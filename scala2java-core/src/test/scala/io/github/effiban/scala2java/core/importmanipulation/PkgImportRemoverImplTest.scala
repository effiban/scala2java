package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Stat, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class PkgImportRemoverImplTest extends UnitTestSuite {

  private val statsByImportSplitter = mock[StatsByImportSplitter]
  private val treeImporterUsed = mock[TreeImporterUsed]

  private val pkgImportRemover = new PkgImportRemoverImpl(
    statsByImportSplitter,
    treeImporterUsed
  )

  test("removeUnusedFrom() when has no initial imports") {
    val pkg = q"package a.b"

    doReturn((Nil, Nil)).when(statsByImportSplitter).split(Nil)

    pkgImportRemover.removeUnusedFrom(pkg).structure shouldBe pkg.structure
  }

  test("removeUnusedFrom() when has initial imports and all used") {
    val initialPkg =
      q"""
      package a.b {
        import c.{D, E}

        trait MyTrait {
          val x: D
          val y: E
        }
      }
      """

    val expectedInitialImporters = List(importer"c.D", importer"c.E")
    val expectedNonImport =
      q"""
      trait MyTrait {
        val x: D
        val y: E
      }         
      """

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.D
        import c.E

        trait MyTrait {
          val x: D
          val y: E
        }
      }
      """

    doReturn((expectedInitialImporters, List(expectedNonImport))).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    expectedInitialImporters.foreach( importer =>
      doReturn(true).when(treeImporterUsed).apply(eqTree(expectedNonImport), eqTree(importer))
    )

    pkgImportRemover.removeUnusedFrom(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("removeUnusedFrom() when has initial imports and some unused") {
    val initialPkg =
      q"""
      package a.b {
        import c.{D, E}
        import f.G
        import h.I

        trait MyTrait1 {
          val x: D
          val y: f.G
        }
        trait MyTrait2 {
          val xx: E
          val yy: h.I
        }
      }
      """

    val expectedInitialImporters = List(importer"c.D", importer"c.E", importer"f.G", importer"h.I")
    val expectedNonImport1 =
      q"""
      trait MyTrait1 {
        val x: D
        val y: f.G
      }
      """
    val expectedNonImport2 =
      q"""
      trait MyTrait2 {
        val xx: E
        val yy: h.I
      }
      """

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.D
        import c.E

        trait MyTrait1 {
          val x: D
          val y: f.G
        }
        trait MyTrait2 {
          val xx: E
          val yy: h.I
        }
      }
      """

    doReturn((expectedInitialImporters, List(expectedNonImport1, expectedNonImport2)))
      .when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((stat: Stat, importer: Importer) => (stat, importer) match {
      case (aStat, anImporter) if aStat.structure == expectedNonImport1.structure && anImporter.structure == importer"c.D".structure => true
      case (aStat, anImporter) if aStat.structure == expectedNonImport2.structure && anImporter.structure == importer"c.E".structure => true
      case _ => false
    }).when(treeImporterUsed).apply(any[Stat], any[Importer])

    pkgImportRemover.removeUnusedFrom(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
