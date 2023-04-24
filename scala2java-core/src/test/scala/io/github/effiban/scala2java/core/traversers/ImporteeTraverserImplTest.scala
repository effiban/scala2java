package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameIndeterminateRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Importee, Name}

class ImporteeTraverserImplTest extends UnitTestSuite {

  private val nameIndeterminateRenderer = mock[NameIndeterminateRenderer]

  private val importeeTraverser = new ImporteeTraverserImpl(nameIndeterminateRenderer)


  test("traverse name") {
    val name = Name.Indeterminate("myName")

    doWrite("myName").when(nameIndeterminateRenderer).render(eqTree(name))

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

    doWrite("origName").when(nameIndeterminateRenderer).render(eqTree(origName))
    doWrite("newName").when(nameIndeterminateRenderer).render(eqTree(newName))

    importeeTraverser.traverse(Importee.Rename(name = origName, rename = newName))

    outputWriter.toString shouldBe "origName /* Renamed in Scala to 'newName' */"
  }

  test("traverse unimported") {
    val name = Name.Indeterminate("myName")

    doWrite("myName").when(nameIndeterminateRenderer).render(eqTree(name))

    importeeTraverser.traverse(Importee.Unimport(name))

    outputWriter.toString shouldBe "myName /* Hidden (unimported) in Scala */"
  }
}
