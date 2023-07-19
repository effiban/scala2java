package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Import, XtensionQuasiquoteImporter}

class ImportRendererImplTest extends UnitTestSuite {

  private val importerRenderer = mock[ImporterRenderer]

  private val importRenderer = new ImportRendererImpl(importerRenderer)

  test("render()") {
    val importer1 = importer"mypackage1.myclass1"
    val importer2 = importer"mypackage2.myclass2"
    val allImporters = List(importer1, importer2)

    doWrite(
      """import mypackage1.myclass1;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(importer1))
    doWrite(
      """import mypackage2.myclass2;
        |""".stripMargin)
      .when(importerRenderer).render(eqTree(importer2))

    importRenderer.render(`import` = Import(allImporters))

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage2.myclass2;
        |""".stripMargin
  }
}
