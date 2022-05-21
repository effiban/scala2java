package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubNameTraverser

import scala.meta.{Importee, Name}

class ImporteeTraverserImplTest extends UnitTestSuite {

  private val importeeTraverser = new ImporteeTraverserImpl(new StubNameTraverser())

  test("traverse name") {
    importeeTraverser.traverse(Importee.Name(Name.Indeterminate("myName")))

    outputWriter.toString shouldBe "myName"
  }

  test("traverse wildcard") {
    importeeTraverser.traverse(Importee.Wildcard())

    outputWriter.toString shouldBe "*"
  }

  test("traverse rename") {
    importeeTraverser.traverse(Importee.Rename(name = Name.Indeterminate("origName"), rename = Name.Indeterminate("newName")))

    outputWriter.toString shouldBe "origName /* Renamed in Scala to 'newName' */"
  }

  test("traverse unimported") {
    importeeTraverser.traverse(Importee.Unimport(name = Name.Indeterminate("myName")))

    outputWriter.toString shouldBe "myName /* Hidden (unimported) in Scala */"
  }
}
