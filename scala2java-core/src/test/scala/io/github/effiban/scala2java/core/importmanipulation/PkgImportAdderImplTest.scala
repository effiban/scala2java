package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class PkgImportAdderImplTest extends UnitTestSuite {

  private val importFlattener = mock[ImportFlattener]
  private val importerDeduplicater = mock[ImporterDeduplicater]
  private val treeImporterGenerator = mock[TreeImporterGenerator]

  private val pkgImportAdder = new PkgImportAdderImpl(
    importFlattener,
    importerDeduplicater,
    treeImporterGenerator
  )

  test("addTo() when has no initial imports and no imports to add") {
    val pkg = q"package a.b"

    doReturn(Nil).when(importFlattener).flatten(Nil)
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

    doReturn(Nil).when(importFlattener).flatten(Nil)
    doReturn(expectedAdditionalImporters).when(treeImporterGenerator).generate(eqTree(initialPkg))
    doReturn(expectedFinalImporters).when(importerDeduplicater).dedup(eqTreeList(expectedAdditionalImporters))

    PkgImportAdder.addTo(initialPkg).structure shouldBe expectedFinalPkg.structure
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

    val expectedInitialImports = List(q"import c.{D, E}", q"import c.D")
    val expectedInitialImporters = List(importer"c.D", importer"c.E", importer"c.D")
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

    doReturn(expectedInitialImporters).when(importFlattener).flatten(expectedInitialImports)
    doReturn(Nil).when(treeImporterGenerator).generate(eqTree(initialPkg))
    doReturn(expectedFinalImporters).when(importerDeduplicater).dedup(eqTreeList(expectedInitialImporters))

    PkgImportAdder.addTo(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("addTo() when has initial imports and also has imports to add") {
    val initialPkg =
      q"""
      package a.b {
        import c.{D, E}
        import c.D

        trait MyTrait {
          val x: c.D
          val y: c.E
          val z: f.G
          val xx: h.I
          val yy: h.I
        }
      }
      """

    val expectedInitialImports = List(q"import c.{D, E}", q"import c.D")
    val expectedInitialImporters = List(importer"c.D", importer"c.E", importer"c.D")
    val expectedAdditionalImporters = List(importer"c.D", importer"c.E", importer"f.G", importer"h.I", importer"h.I")
    val expectedFinalImporters = List(importer"c.D", importer"c.E", importer"f.G", importer"h.I")

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.D
        import c.E
        import f.G
        import h.I

        trait MyTrait {
          val x: c.D
          val y: c.E
          val z: f.G
          val xx: h.I
          val yy: h.I
        }
      }
      """

    doReturn(expectedInitialImporters).when(importFlattener).flatten(expectedInitialImports)
    doReturn(expectedAdditionalImporters).when(treeImporterGenerator).generate(eqTree(initialPkg))
    doReturn(expectedFinalImporters).when(importerDeduplicater).dedup(eqTreeList(expectedInitialImporters ++ expectedAdditionalImporters))

    PkgImportAdder.addTo(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
