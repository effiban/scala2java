package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList

import scala.meta.{XtensionQuasiquoteImporter, XtensionQuasiquoteTerm}

class ImporterCollectorImplTest extends UnitTestSuite {

  private val importFlattener = mock[ImportFlattener]

  private val importerCollector = new ImporterCollectorImpl(importFlattener)

  test("collectFlat when stats empty") {
    doReturn(Nil).when(importFlattener).flatten(Nil)

    importerCollector.collectFlat(Nil) shouldBe Nil
  }

  test("collectFlat when has one stat and it is an Import") {
    val imports = List(q"import a.b")
    val expectedImporters = List(importer"a.b")

    doReturn(expectedImporters).when(importFlattener).flatten(eqTreeList(imports))

    importerCollector.collectFlat(imports).structure shouldBe expectedImporters.structure
  }

  test("collectFlat when has two stats, both Imports") {
    val imports = List(q"import a.b", q"import c.d")
    val expectedImporters = List(importer"a.b", importer"c.d")

    doReturn(expectedImporters).when(importFlattener).flatten(eqTreeList(imports))

    importerCollector.collectFlat(imports).structure shouldBe expectedImporters.structure
  }

  test("collectFlat when has non-Imports and Imports") {
    val import1 = q"import a.b"
    val import2 = q"import c.d"
    val imports = List(import1, import2)
    val stats = List(q"val x: Int = 3", import1, q"foo(2)", import2)
    val expectedImporters = List(importer"a.b", importer"c.d")

    doReturn(expectedImporters).when(importFlattener).flatten(eqTreeList(imports))

    importerCollector.collectFlat(stats).structure shouldBe expectedImporters.structure
  }

  test("collectFlat when has non-Imports only") {
    val stats = List(q"val x: Int = 3", q"foo(2)")

    doReturn(Nil).when(importFlattener).flatten(Nil)

    importerCollector.collectFlat(stats) shouldBe Nil
  }
}
