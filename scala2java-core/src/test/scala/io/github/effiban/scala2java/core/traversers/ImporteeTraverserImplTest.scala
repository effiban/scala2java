package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Importee, Name}

class ImporteeTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]

  private val importeeTraverser = new ImporteeTraverserImpl(nameTraverser)


  test("traverse name") {
    val name = Name.Indeterminate("myName")

    doWrite("myName").when(nameTraverser).traverse(eqTree(name))

    importeeTraverser.traverse(Importee.Name(name))

    outputWriter.toString shouldBe "myName"
  }

  test("traverse wildcard") {
    importeeTraverser.traverse(Importee.Wildcard())

    outputWriter.toString shouldBe "*"
  }

  test("traverse rename") {
    val origName = Name.Indeterminate("origName")
    val newName = Name.Indeterminate("newName")

    doWrite("origName").when(nameTraverser).traverse(eqTree(origName))
    doWrite("newName").when(nameTraverser).traverse(eqTree(newName))

    importeeTraverser.traverse(Importee.Rename(name = origName, rename = newName))

    outputWriter.toString shouldBe "origName /* Renamed in Scala to 'newName' */"
  }

  test("traverse unimported") {
    val name = Name.Indeterminate("myName")

    doWrite("myName").when(nameTraverser).traverse(eqTree(name))

    importeeTraverser.traverse(Importee.Unimport(name))

    outputWriter.toString shouldBe "myName /* Hidden (unimported) in Scala */"
  }
}