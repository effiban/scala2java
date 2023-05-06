package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Importee, Importer, Name, Term, XtensionQuasiquoteTerm}

class ImporterRendererImplTest extends UnitTestSuite {

  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val importeeRenderer = mock[ImporteeRenderer]

  private val importerRenderer = new ImporterRendererImpl(
    defaultTermRefRenderer,
    importeeRenderer
  )


  test("render when there is one importee") {
    val termRef = q"mypackage"
    val importee = Importee.Name(Name.Indeterminate("myclass"))

    val importer = Importer(
      ref = termRef,
      importees = List(importee)
    )

    doWrite("mypackage").when(defaultTermRefRenderer).render(eqTree(termRef))
    doWrite("myclass").when(importeeRenderer).render(eqTree(importee))

    importerRenderer.render(importer)

    outputWriter.toString shouldBe
      """import mypackage.myclass;
        |""".stripMargin
  }

  test("render when there are two importees") {
    val termRef = q"mypackage"
    val importee1 = Importee.Name(Name.Indeterminate("myclass1"))
    val importee2 = Importee.Name(Name.Indeterminate("myclass2"))

    val importer = Importer(
      ref = Term.Name("mypackage"),
      importees = List(importee1, importee2)
    )

    doWrite("mypackage").when(defaultTermRefRenderer).render(eqTree(termRef))
    doWrite("myclass1").when(importeeRenderer).render(eqTree(importee1))
    doWrite("myclass2").when(importeeRenderer).render(eqTree(importee2))

    importerRenderer.render(importer)

    outputWriter.toString shouldBe
      """import mypackage.myclass1;
        |import mypackage.myclass2;
        |""".stripMargin
  }
}
