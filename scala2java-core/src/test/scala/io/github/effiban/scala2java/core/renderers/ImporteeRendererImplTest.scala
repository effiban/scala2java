package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Importee, Name}

class ImporteeRendererImplTest extends UnitTestSuite {

  private val nameIndeterminateRenderer = mock[NameIndeterminateRenderer]

  private val importeeRenderer = new ImporteeRendererImpl(nameIndeterminateRenderer)


  test("render name") {
    val name = Name.Indeterminate("myName")

    doWrite("myName").when(nameIndeterminateRenderer).render(eqTree(name))

    importeeRenderer.render(Importee.Name(name))

    outputWriter.toString shouldBe "myName"
  }

  test("render wildcard") {
    importeeRenderer.render(Importee.Wildcard())

    outputWriter.toString shouldBe "*"
  }

  test("render rename") {
    val origName = Name.Indeterminate("origName")
    val newName = Name.Indeterminate("newName")

    doWrite("origName").when(nameIndeterminateRenderer).render(eqTree(origName))
    doWrite("newName").when(nameIndeterminateRenderer).render(eqTree(newName))

    importeeRenderer.render(Importee.Rename(name = origName, rename = newName))

    outputWriter.toString shouldBe "origName /* Renamed in Scala to 'newName' */"
  }

  test("render unimported") {
    val name = Name.Indeterminate("myName")

    doWrite("myName").when(nameIndeterminateRenderer).render(eqTree(name))

    importeeRenderer.render(Importee.Unimport(name))

    outputWriter.toString shouldBe "myName /* Hidden (unimported) in Scala */"
  }
}
