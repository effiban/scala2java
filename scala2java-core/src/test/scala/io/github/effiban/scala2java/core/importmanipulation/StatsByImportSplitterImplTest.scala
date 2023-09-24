package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class StatsByImportSplitterImplTest extends UnitTestSuite {

  private val importFlattener = mock[ImportFlattener]

  private val statsByImportSplitter = new StatsByImportSplitterImpl(importFlattener)

  test("split when stats empty") {
    doReturn(Nil).when(importFlattener).flatten(Nil)

    statsByImportSplitter.split(Nil) shouldBe (Nil, Nil)
  }

  test("split when has one stat and it is an Import") {
    val imports = List(q"import a.b")
    val expectedImporters = List(importer"a.b")

    doReturn(expectedImporters).when(importFlattener).flatten(eqTreeList(imports))

    val (actualImporters, actualNonImports) = statsByImportSplitter.split(imports)
    actualImporters.structure shouldBe expectedImporters.structure
    actualNonImports shouldBe Nil
  }

  test("split when has two stats, both Imports") {
    val imports = List(q"import a.b", q"import c.d")
    val expectedImporters = List(importer"a.b", importer"c.d")

    doReturn(expectedImporters).when(importFlattener).flatten(eqTreeList(imports))

    val (actualImporters, actualNonImports) = statsByImportSplitter.split(imports)
    actualImporters.structure shouldBe expectedImporters.structure
    actualNonImports shouldBe Nil
  }

  test("split when has non-Imports and Imports") {
    val import1 = q"import a.b"
    val import2 = q"import c.d"
    val imports = List(import1, import2)
    val stats = List(q"val x: Int = 3", import1, q"foo(2)", import2)
    val expectedImporters = List(importer"a.b", importer"c.d")
    val expectedNonImports = List(q"val x: Int = 3", q"foo(2)")

    doReturn(expectedImporters).when(importFlattener).flatten(eqTreeList(imports))

    val (actualImporters, actualNonImports) = statsByImportSplitter.split(stats)
    actualImporters.structure shouldBe expectedImporters.structure
    actualNonImports.structure shouldBe expectedNonImports.structure
  }

  test("split when has non-Imports only") {
    val stats = List(q"val x: Int = 3", q"foo(2)")

    doReturn(Nil).when(importFlattener).flatten(Nil)

    val (actualImporters, actualNonImports) = statsByImportSplitter.split(stats)
    actualImporters shouldBe Nil
    actualNonImports.structure shouldBe stats.structure
  }
}
