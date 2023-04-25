package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Name
import scala.meta.Term.This

class ThisRendererImplTest extends UnitTestSuite {

  private val nameRenderer = mock[NameRenderer]

  private val thisRenderer = new ThisRendererImpl(nameRenderer)

  test("render() when name is anonymous") {
    thisRenderer.render(This(Name.Anonymous()))

    outputWriter.toString shouldBe "this"
  }

  test("render() when name is specified") {
    val name = Name.Indeterminate("EnclosingClass")

    doWrite("EnclosingClass").when(nameRenderer).render(eqTree(name))

    thisRenderer.render(This(name))

    outputWriter.toString shouldBe "EnclosingClass.this"
  }
}
