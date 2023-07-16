package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ImportRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Import, Importee, Importer, Name, Term, XtensionQuasiquoteImporter}

class ImportRendererImplTest extends UnitTestSuite {

  private val importerRenderer = mock[ImporterRenderer]

  private val importRenderer = new ImportRendererImpl(importerRenderer)

  test("render() when asComment=false should write the import") {
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

  test("traverse() when asComment=true should write a comment") {
    val importer = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )

    importRenderer.render(`import` = Import(List(importer)), context = ImportRenderContext(asComment = true))

    outputWriter.toString shouldBe "/* import mypackage1.myclass1 */"
  }
}
