package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Stat, Tree, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class PkgImportAdderImplTest extends UnitTestSuite {

  private val statsByImportSplitter = mock[StatsByImportSplitter]
  private val importerDeduplicater = mock[ImporterDeduplicater]
  private val treeImporterGenerator = mock[TreeImporterGenerator]

  private val pkgImportAdder = new PkgImportAdderImpl(
    statsByImportSplitter,
    importerDeduplicater,
    treeImporterGenerator
  )

  test("addTo() when has no initial imports and no imports to add") {
    val pkg = q"package a.b"

    doReturn((Nil, Nil)).when(statsByImportSplitter).split(Nil)
    doReturn(Nil).when(treeImporterGenerator).generate(eqTree(pkg))
    doReturn(Nil).when(importerDeduplicater).dedup(Nil)

    pkgImportAdder.addTo(pkg).structure shouldBe pkg.structure
  }

  test("addTo() when has no initial imports but has imports to add") {
    val initialPkg =
      q"""
      package a.b {

        trait MyTrait {
          val x: c.D
          val y: e.F
          val z: e.F
        }
      }
      """

    val expectedNonImport =
      q"""
      trait MyTrait {
        val x: c.D
        val y: e.F
        val z: e.F
      }
      """
    val expectedAdditionalImporters = List(importer"c.D", importer"e.F", importer"e.F")
    val expectedFinalImporters = List(importer"c.D", importer"e.F")

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.D
        import e.F

        trait MyTrait {
          val x: c.D
          val y: e.F
          val z: e.F
        }
      }
      """

    doReturn((Nil, initialPkg.stats)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doReturn(expectedAdditionalImporters).when(treeImporterGenerator).generate(eqTree(expectedNonImport))
    doReturn(expectedFinalImporters).when(importerDeduplicater).dedup(eqTreeList(expectedAdditionalImporters))

    pkgImportAdder.addTo(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("addTo() when has initial imports but has no imports to add") {
    val initialPkg =
      q"""
      package a.b {
        import c.{D, E}
        import c.D

        trait MyTrait {
          val x: D
          val y: E
        }
      }
      """

    val expectedInitialImporters = List(importer"c.D", importer"c.E", importer"c.D")
    val expectedNonImport =
      q"""
      trait MyTrait {
        val x: D
        val y: E
      }
       """

    val expectedFinalImporters = List(importer"c.D", importer"c.E")

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
    doReturn(Nil).when(treeImporterGenerator).generate(eqTree(expectedNonImport))
    doReturn(expectedFinalImporters).when(importerDeduplicater).dedup(eqTreeList(expectedInitialImporters))

    pkgImportAdder.addTo(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("addTo() when has initial imports and also has imports to add") {
    val initialPkg =
      q"""
      package a.b {
        import c.{D, E}
        import c.D

        trait MyTrait1 {
          val x: c.D
          val y: c.E
          val z: f.G
        }
        trait MyTrait2 {
          val xx: h.I
          val yy: h.I
        }
      }
      """

    val expectedInitialImporters = List(importer"c.D", importer"c.E", importer"c.D")
    val expectedNonImport1 =
      q"""
      trait MyTrait1 {
        val x: c.D
        val y: c.E
        val z: f.G
      }
      """
    val expectedNonImport2 =
      q"""
      trait MyTrait2 {
        val xx: h.I
        val yy: h.I
      }
      """

    val expectedAdditionalImporters = List(importer"c.D", importer"c.E", importer"f.G", importer"h.I", importer"h.I")
    val expectedFinalImporters = List(importer"c.D", importer"c.E", importer"f.G", importer"h.I")

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.D
        import c.E
        import f.G
        import h.I

        trait MyTrait1 {
          val x: c.D
          val y: c.E
          val z: f.G
        }
        trait MyTrait2 {
          val xx: h.I
          val yy: h.I
        }
      }
      """

    doReturn((expectedInitialImporters, List(expectedNonImport1, expectedNonImport2)))
      .when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((stat: Stat) => stat match {
      case aStat if aStat.structure == expectedNonImport1.structure => List(importer"c.D", importer"c.E", importer"f.G")
      case aStat if aStat.structure == expectedNonImport2.structure => List(importer"h.I", importer"h.I")
      case _ => Nil
    }).when(treeImporterGenerator).generate(any[Tree])
    doReturn(expectedFinalImporters).when(importerDeduplicater).dedup(eqTreeList(expectedInitialImporters ++ expectedAdditionalImporters))

    pkgImportAdder.addTo(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
