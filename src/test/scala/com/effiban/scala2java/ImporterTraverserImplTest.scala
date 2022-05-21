package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubImporteeTraverser, StubTermRefTraverser}

import scala.meta.{Importee, Importer, Name, Term}

class ImporterTraverserImplTest extends UnitTestSuite {

  private val importerTraverser = new ImporterTraverserImpl(new StubTermRefTraverser(), new StubImporteeTraverser())

  test("traverse when not a 'scala' importer, and there is one importee") {
    val importer = Importer(
      ref = Term.Name("mypackage"),
      importees = List(Importee.Name(Name.Indeterminate("myclass")))
    )
    importerTraverser.traverse(importer)

    outputWriter.toString shouldBe
      """import mypackage.myclass;
        |""".stripMargin
  }

  test("traverse when not a 'scala' importer, and there are two importees") {
    val importer = Importer(
      ref = Term.Name("mypackage"),
      importees = List(
        Importee.Name(Name.Indeterminate("myclass1")),
        Importee.Name(Name.Indeterminate("myclass2"))
      )
    )
    importerTraverser.traverse(importer)

    outputWriter.toString shouldBe
      """import mypackage.myclass1;
        |import mypackage.myclass2;
        |""".stripMargin
  }

  test("traverse when ref is 'scala' should skip it") {
    val importer = Importer(
      ref = Term.Name("scala"),
      importees = List(
        Importee.Name(Name.Indeterminate("myclass1")),
        Importee.Name(Name.Indeterminate("myclass2"))
      )
    )
    importerTraverser.traverse(importer)

    outputWriter.toString shouldBe ""
  }

  test("traverse when ref starts with 'scala' should skip it") {
    val importer = Importer(
      ref = Term.Select(Term.Name("scala"), Term.Name("pkg")),
      importees = List(
        Importee.Name(Name.Indeterminate("myclass1")),
        Importee.Name(Name.Indeterminate("myclass2"))
      )
    )
    importerTraverser.traverse(importer)

    outputWriter.toString shouldBe ""
  }
}
