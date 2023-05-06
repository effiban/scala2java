package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Import, Importee, Importer, Name, Term, XtensionQuasiquoteImporter}

class ImportRendererImplTest extends UnitTestSuite {

  private val PackageStatContext = StatContext(JavaScope.Package)

  private val importerRenderer = mock[ImporterRenderer]

  private val importRenderer = new ImportRendererImpl(importerRenderer)

  test("traverse() in package scope") {
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

    importRenderer.render(`import` = Import(allImporters), context = PackageStatContext)

    outputWriter.toString shouldBe
      """import mypackage1.myclass1;
        |import mypackage2.myclass2;
        |""".stripMargin
  }

  test("traverse() in class scope should write a comment") {
    val importer = Importer(
      ref = Term.Name("mypackage1"),
      importees = List(Importee.Name(Name.Indeterminate("myclass1")))
    )

    importRenderer.render(`import` = Import(List(importer)), context = StatContext(JavaScope.Class))

    outputWriter.toString shouldBe "/* import mypackage1.myclass1 */"
  }
}
