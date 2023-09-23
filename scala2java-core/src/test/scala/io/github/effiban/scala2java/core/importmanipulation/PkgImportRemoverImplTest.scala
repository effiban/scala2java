package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Tree, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class PkgImportRemoverImplTest extends UnitTestSuite {

  private val importerCollector = mock[ImporterCollector]
  private val treeImporterMatchesPredicate = mock[TreeImporterUsed]

  private val pkgImportRemover = new PkgImportRemoverImpl(
    importerCollector,
    treeImporterMatchesPredicate
  )

  test("removeFrom() when has no initial imports") {
    val pkg = q"package a.b"

    doReturn(Nil).when(importerCollector).collectFlat(Nil)

    pkgImportRemover.removeUnusedFrom(pkg).structure shouldBe pkg.structure
  }

  test("removeFrom() when has initial imports and all used") {
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

    doReturn(expectedInitialImporters).when(importerCollector).collectFlat(eqTreeList(initialPkg.stats))
    expectedInitialImporters.foreach( importer =>
      doReturn(false).when(treeImporterMatchesPredicate).apply(eqTree(initialPkg), eqTree(importer))
    )

    pkgImportRemover.removeUnusedFrom(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("removeFrom() when has initial imports and some unused") {
    val initialPkg =
      q"""
      package a.b {
        import c.{D, E}
        import f.G
        import h.I

        trait MyTrait {
          val x: D
          val y: E
          val z: f.G
          val xx: h.I
        }
      }
      """

    val expectedInitialImporters = List(importer"c.D", importer"c.E", importer"f.G", importer"h.I")

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.D
        import c.E

        trait MyTrait {
          val x: D
          val y: E
          val z: f.G
          val xx: h.I
        }
      }
      """

    doReturn(expectedInitialImporters).when(importerCollector).collectFlat(eqTreeList(initialPkg.stats))
    doAnswer((_: Tree, importer: Importer) => importer match {
      case anImporter if anImporter.structure == importer"f.G".structure => true
      case anImporter if anImporter.structure == importer"h.I".structure => true
      case _ => false
    }).when(treeImporterMatchesPredicate).apply(eqTree(initialPkg), any[Importer])

    pkgImportRemover.removeUnusedFrom(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
