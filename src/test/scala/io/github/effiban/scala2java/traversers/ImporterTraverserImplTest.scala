package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Importee, Importer, Name, Term}

class ImporterTraverserImplTest extends UnitTestSuite {

  private val termRefTraverser = mock[TermRefTraverser]
  private val importeeTraverser = mock[ImporteeTraverser]

  private val importerTraverser = new ImporterTraverserImpl(termRefTraverser, importeeTraverser)


  test("traverse when there is one importee") {
    val termRef = Term.Name("mypackage")
    val importee = Importee.Name(Name.Indeterminate("myclass"))

    val importer = Importer(
      ref = termRef,
      importees = List(importee)
    )

    doWrite("mypackage").when(termRefTraverser).traverse(eqTree(termRef))
    doWrite("myclass").when(importeeTraverser).traverse(eqTree(importee))

    importerTraverser.traverse(importer)

    outputWriter.toString shouldBe
      """import mypackage.myclass;
        |""".stripMargin
  }

  test("traverse when there are two importees") {
    val termRef = Term.Name("mypackage")
    val importee1 = Importee.Name(Name.Indeterminate("myclass1"))
    val importee2 = Importee.Name(Name.Indeterminate("myclass2"))

    val importer = Importer(
      ref = Term.Name("mypackage"),
      importees = List(importee1, importee2)
    )

    doWrite("mypackage").when(termRefTraverser).traverse(eqTree(termRef))
    doWrite("myclass1").when(importeeTraverser).traverse(eqTree(importee1))
    doWrite("myclass2").when(importeeTraverser).traverse(eqTree(importee2))

    importerTraverser.traverse(importer)

    outputWriter.toString shouldBe
      """import mypackage.myclass1;
        |import mypackage.myclass2;
        |""".stripMargin
  }
}
