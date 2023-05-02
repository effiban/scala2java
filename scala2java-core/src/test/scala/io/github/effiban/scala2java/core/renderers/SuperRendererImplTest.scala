package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Name
import scala.meta.Term.Super

class SuperRendererImplTest extends UnitTestSuite {

  private val nameRenderer = mock[NameRenderer]

  private val superRenderer = new SuperRendererImpl(nameRenderer)


  test("traverse() without clauses") {
    val `super` = Super(thisp = Name.Anonymous(), superp = Name.Anonymous())

    superRenderer.render(`super`)

    outputWriter.toString shouldBe "super"
  }

  test("traverse() with 'thisp' clause only") {
    val name = Name.Indeterminate("EnclosingClass")

    doWrite("EnclosingClass").when(nameRenderer).render(eqTree(name))

    superRenderer.render(Super(thisp = name, superp = Name.Anonymous()))

    outputWriter.toString shouldBe "EnclosingClass.super"
  }

  test("traverse() with both clauses") {
    val thisName = Name.Indeterminate("EnclosingClass")
    val superName = Name.Indeterminate("SuperTrait")

    doWrite("EnclosingClass").when(nameRenderer).render(eqTree(thisName))

    superRenderer.render(Super(thisp = thisName, superp = superName))

    outputWriter.toString shouldBe "EnclosingClass.super/* extends SuperTrait */"
  }

  test("traverse() with 'superp' clause only") {
    val superName = Name.Indeterminate("SuperTrait")

    superRenderer.render(Super(thisp = Name.Anonymous(), superp = superName))

    outputWriter.toString shouldBe "super/* extends SuperTrait */"
  }
}
