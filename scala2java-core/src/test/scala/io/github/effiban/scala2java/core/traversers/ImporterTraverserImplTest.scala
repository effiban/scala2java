package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.DefaultTermRefRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Importee, Importer, Name, Term, XtensionQuasiquoteTerm}

class ImporterTraverserImplTest extends UnitTestSuite {

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val importeeTraverser = mock[ImporteeTraverser]

  private val importerTraverser = new ImporterTraverserImpl(
    defaultTermRefTraverser,
    defaultTermRefRenderer,
    importeeTraverser
  )


  test("traverse when there is one importee") {
    val termRef = q"mypackage"
    val traversedTermRef = q"mytraversedpackage"
    val importee = Importee.Name(Name.Indeterminate("myclass"))

    val importer = Importer(
      ref = termRef,
      importees = List(importee)
    )

    doReturn(traversedTermRef).when(defaultTermRefTraverser).traverse(eqTree(termRef))
    doWrite("mytraversedpackage").when(defaultTermRefRenderer).render(eqTree(traversedTermRef))
    doWrite("myclass").when(importeeTraverser).traverse(eqTree(importee))

    importerTraverser.traverse(importer)

    outputWriter.toString shouldBe
      """import mytraversedpackage.myclass;
        |""".stripMargin
  }

  test("traverse when there are two importees") {
    val termRef = q"mypackage"
    val traversedTermRef = q"mytraversedpackage"
    val importee1 = Importee.Name(Name.Indeterminate("myclass1"))
    val importee2 = Importee.Name(Name.Indeterminate("myclass2"))

    val importer = Importer(
      ref = Term.Name("mypackage"),
      importees = List(importee1, importee2)
    )

    doReturn(traversedTermRef).when(defaultTermRefTraverser).traverse(eqTree(termRef))
    doWrite("mytraversedpackage").when(defaultTermRefRenderer).render(eqTree(traversedTermRef))
    doWrite("myclass1").when(importeeTraverser).traverse(eqTree(importee1))
    doWrite("myclass2").when(importeeTraverser).traverse(eqTree(importee2))

    importerTraverser.traverse(importer)

    outputWriter.toString shouldBe
      """import mytraversedpackage.myclass1;
        |import mytraversedpackage.myclass2;
        |""".stripMargin
  }
}
